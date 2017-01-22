package me.Bleuzen.RPGHealthPlus;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_11_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;

import net.minecraft.server.v1_11_R1.EnumParticle;
import net.minecraft.server.v1_11_R1.PacketPlayOutWorldParticles;

public class Utils {
	
	private static DecimalFormat doubleFormat = new DecimalFormat("#.##", new DecimalFormatSymbols(Locale.ENGLISH));

	static void showParticles(Player p) {
		for (Player pa : Bukkit.getOnlinePlayers()) {
			((CraftPlayer) pa).getHandle().playerConnection.sendPacket(new PacketPlayOutWorldParticles(
					EnumParticle.HEART, true, (float) p.getLocation().getX(), (float) p.getLocation().getY() + 1.0F,
					(float) p.getLocation().getZ(), 1.0F, 1.0F, 1.0F, 1.0F, 10, null));
		}
	}
	
	static void showHologram(Location loc, String txt) {
		final Hologram holo = HologramsAPI.createHologram(Main.getInstance(), loc);
		holo.appendTextLine(txt);
		Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), new Runnable() {	
			@Override
			public void run() {
				holo.delete();
			}
		}, Main.getInstance().cfg.getInt("configuration.xp-holograms-time"));
	}
	
	static double evaluate(String input) {
		return formatDouble(eval(input));
	}
	
	// Source: http://stackoverflow.com/a/26227947/6627190
	private static double eval(final String str) {
	    return new Object() {
	        int pos = -1, ch;

	        void nextChar() {
	            ch = (++pos < str.length()) ? str.charAt(pos) : -1;
	        }

	        boolean eat(int charToEat) {
	            while (ch == ' ') nextChar();
	            if (ch == charToEat) {
	                nextChar();
	                return true;
	            }
	            return false;
	        }

	        double parse() {
	            nextChar();
	            double x = parseExpression();
	            if (pos < str.length()) throw new RuntimeException("Unexpected: " + (char)ch);
	            return x;
	        }

	        // Grammar:
	        // expression = term | expression `+` term | expression `-` term
	        // term = factor | term `*` factor | term `/` factor
	        // factor = `+` factor | `-` factor | `(` expression `)`
	        //        | number | functionName factor | factor `^` factor

	        double parseExpression() {
	            double x = parseTerm();
	            for (;;) {
	                if      (eat('+')) x += parseTerm(); // addition
	                else if (eat('-')) x -= parseTerm(); // subtraction
	                else return x;
	            }
	        }

	        double parseTerm() {
	            double x = parseFactor();
	            for (;;) {
	                if      (eat('*')) x *= parseFactor(); // multiplication
	                else if (eat('/')) x /= parseFactor(); // division
	                else return x;
	            }
	        }

	        double parseFactor() {
	            if (eat('+')) return parseFactor(); // unary plus
	            if (eat('-')) return -parseFactor(); // unary minus

	            double x;
	            int startPos = this.pos;
	            if (eat('(')) { // parentheses
	                x = parseExpression();
	                eat(')');
	            } else if ((ch >= '0' && ch <= '9') || ch == '.') { // numbers
	                while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
	                x = Double.parseDouble(str.substring(startPos, this.pos));
	            } else if (ch >= 'a' && ch <= 'z') { // functions
	                while (ch >= 'a' && ch <= 'z') nextChar();
	                String func = str.substring(startPos, this.pos);
	                x = parseFactor();
	                if (func.equals("sqrt")) x = Math.sqrt(x);
	                else if (func.equals("sin")) x = Math.sin(Math.toRadians(x));
	                else if (func.equals("cos")) x = Math.cos(Math.toRadians(x));
	                else if (func.equals("tan")) x = Math.tan(Math.toRadians(x));
	                else throw new RuntimeException("Unknown function: " + func);
	            } else {
	                throw new RuntimeException("Unexpected: " + (char)ch);
	            }

	            if (eat('^')) x = Math.pow(x, parseFactor()); // exponentiation

	            return x;
	        }
	    }.parse();
	}
	
	static Double formatDouble(double in) {
		return Double.parseDouble(formatDoubleToString(in));
	}

	static String formatDoubleToString(double in) {
		return doubleFormat.format(in);
	}

	static int toInt(double in) {
		return (int) Math.round(in);
	}

}