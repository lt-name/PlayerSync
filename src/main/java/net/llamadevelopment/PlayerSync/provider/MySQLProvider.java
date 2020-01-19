package net.llamadevelopment.PlayerSync.provider;

import cn.nukkit.Player;
import cn.nukkit.utils.Config;
import net.llamadevelopment.PlayerSync.PlayerSync;
import net.llamadevelopment.PlayerSync.utils.ItemAPI;
import net.llamadevelopment.PlayerSync.utils.SyncPlayer;

import java.sql.*;

public class MySQLProvider extends Provider {

    private Connection connection;
    public String database;

    @Override
    public void open(Config c) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            database = c.getString("mysql.database");
            String connectionUri = "jdbc:mysql://" + c.getString("mysql.ip") + ":" + c.getString("mysql.port") + "/" + c.getString("mysql.database");

            connection = DriverManager.getConnection(connectionUri, c.getString("mysql.username"), c.getString("mysql.password"));
            connection.setAutoCommit(true);

            DatabaseMetaData dbm = null;
            dbm = connection.getMetaData();
            ResultSet tables = dbm.getTables(null, null, "players", null);
            if (!tables.next()) {
                String tableCreate = "CREATE TABLE players (id VARCHAR(64), inventory TEXT null, enderchest TEXT null, health VARCHAR(64) null, food INT(64) null, level INT(64), exp INT(64), constraint id_pk primary key(id))";
                Statement createTable = connection.createStatement();
                createTable.executeUpdate(tableCreate);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            System.out.println("It was not possible to establish a connection with the database.");
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
            System.out.println("MySQL Driver is missing... Have you installed DbLib correctly?");
        }
    }

    @Override
    public void close() {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public SyncPlayer getPlayer(Player player) {
        try {
            ResultSet res = connection.createStatement().executeQuery("SELECT * FROM " + database + ".players WHERE id='" + player.getUniqueId().toString() + "'");
            if (res.next()) {
                return new SyncPlayer(res.getString("inventory"), res.getString("enderchest"), Float.parseFloat(res.getString("health")), res.getInt("food"), res.getInt("level"), res.getInt("exp"));
            } else {
                String inv = "empty";
                String ecInv = "empty";

                if (player.getInventory().getContents().size() > 0) {
                    inv = ItemAPI.invToString(player.getInventory());
                }

                if (player.getEnderChestInventory().getContents().size() > 0) {
                    ecInv = ItemAPI.invToString(player.getEnderChestInventory());
                }

                savePlayer(player.getUniqueId().toString(), inv, ecInv, "20.0", 20, 0, 0);
                return new SyncPlayer(inv, ecInv, 20.0f, 20, 0, 0);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    @Override
    public void savePlayer(String uuid, String invString, String ecString, String health, int food, int level, int exp) {
        try {
            ResultSet res = connection.createStatement().executeQuery("SELECT * FROM " + database + ".players WHERE id='" + uuid + "'");
            if (res.next()) {
                PreparedStatement upt = connection.prepareStatement("UPDATE " + database + ".players SET inventory = ?, enderchest = ?, health = ?, food = ?, level = ?, exp = ? WHERE id='" + uuid + "'" );
                upt.setString(1, invString);
                upt.setString(2, ecString);
                upt.setString(3, health);
                upt.setInt(4, food);
                upt.setInt(5, level);
                upt.setInt(6, exp);
                upt.executeUpdate();
            } else {
                PreparedStatement newUser = connection.prepareStatement("INSERT INTO " + database + ".players (id, inventory, enderchest, health, food, level, exp) VALUES (?, ?, ?, ?, ?, ?, ?)");
                newUser.setString(1, uuid);
                newUser.setString(2, invString);
                newUser.setString(3, ecString);
                newUser.setString(4, health);
                newUser.setInt(5, food);
                newUser.setInt(6, level);
                newUser.setInt(7, exp);
                newUser.executeUpdate();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public String getName() {
        return "mysql";
    }
}
