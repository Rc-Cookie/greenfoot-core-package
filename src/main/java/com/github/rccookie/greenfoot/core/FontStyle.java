package com.github.rccookie.greenfoot.core;

import greenfoot.Font;

public abstract class FontStyle extends Font {

    /**
     * Indicates weather the current session is online or on the Greenfoot application.
     * Offline the code runs plain java ansuring that any java functionallity will work.
     * Online however the code gets converted to javascript which is not very reliable
     * and does not have all classes that java has. Therefore special handling when
     * operating online max be helpful or neccecary.
     */
    public static final boolean IS_ONLINE = CoreWorld.IS_ONLINE;

    private final double newLineDim;

    private FontStyle(String name, boolean bold, boolean italic, int size, double newLineDim) {
        super(name, bold, italic, size);
        this.newLineDim = newLineDim;
    }

    public int getWidth(String string) {
        int maxWidth = 0;
        double current = 0;
        for(int i=0; i<string.length(); i++) {
            char c = string.charAt(i);
            if(c == '\n') {
                if(current > maxWidth) maxWidth = (int)current;
                current = 0;
            }
            else current += getCharWidth(c) * getSize();
        }
        return Math.max(maxWidth, (int)current);
    }

    protected abstract double getCharWidth(char c);

    public int getHeight(String string) {
        int nLines = numberOfLines(string);
        return getSize() * nLines + (int)(getSize() * (nLines - 1) * newLineDim);
    }



    public static final FontStyle monospace(int size) {
        return monospace(size, false, false);
    }

    public static final FontStyle monospace(int size, boolean bold, boolean italic) {
        System.out.println("Is online: " + IS_ONLINE);
        return new FontStyle("Consolas", bold, italic, size, 0.2) {
            @Override
            protected double getCharWidth(char c) {
                return IS_ONLINE ? 0.5 : 0.567;
            }
        };
    }



    public static final FontStyle modern(int size) {
        return modern(size, false, false);
    }

    public static final FontStyle modern(int size, boolean bold, boolean italic) {
        return new FontStyle("Segoe UI", bold, italic, size, 0.38) {
            @Override
            protected double getCharWidth(char c) {
                // I know that switch exists but its ugly in Java 11
                if(bold) {
                    if(c == ' ') return 0.3; // is actually bigger
                    if(c == 'a') return 0.55;
                    if(c == 'b') return 0.6;
                    if(c == 'c') return 0.5;
                    if(c == 'd') return 0.6;
                    if(c == 'e') return 0.55;
                    if(c == 'f') return 0.4;
                    if(c == 'g') return 0.6;
                    if(c == 'h') return 0.6;
                    if(c == 'i') return 0.3;
                    if(c == 'j') return 0.3;
                    if(c == 'k') return 0.55;
                    if(c == 'l') return 0.3;
                    if(c == 'm') return 0.9;
                    if(c == 'n') return 0.6;
                    if(c == 'o') return 0.6;
                    if(c == 'p') return 0.6;
                    if(c == 'q') return 0.6;
                    if(c == 'r') return 0.4;
                    if(c == 's') return 0.45;
                    if(c == 't') return 0.4;
                    if(c == 'u') return 0.6;
                    if(c == 'v') return 0.55;
                    if(c == 'w') return 0.8;
                    if(c == 'x') return 0.55;
                    if(c == 'y') return 0.55;
                    if(c == 'z') return 0.5;
                    if(c == 'A') return 0.7;
                    if(c == 'B') return 0.65;
                    if(c == 'C') return 0.6;
                    if(c == 'D') return 0.75;
                    if(c == 'E') return 0.55;
                    if(c == 'F') return 0.5;
                    if(c == 'G') return 0.7;
                    if(c == 'H') return 0.75;
                    if(c == 'I') return 0.3;
                    if(c == 'J') return 0.45;
                    if(c == 'K') return 0.65;
                    if(c == 'L') return 0.5;
                    if(c == 'M') return 0.95;
                    if(c == 'N') return 0.8;
                    if(c == 'O') return 0.75;
                    if(c == 'P') return 0.6;
                    if(c == 'Q') return 0.75;
                    if(c == 'R') return 0.65;
                    if(c == 'S') return 0.55;
                    if(c == 'T') return 0.6;
                    if(c == 'U') return 0.7;
                    if(c == 'V') return 0.65;
                    if(c == 'W') return 1;
                    if(c == 'X') return 0.65;
                    if(c == 'Y') return 0.6;
                    if(c == 'Z') return 0.6;
                    if(c == '0') return 0.6;
                    if(c == '1') return 0.6;
                    if(c == '2') return 0.6;
                    if(c == '3') return 0.6;
                    if(c == '4') return 0.6;
                    if(c == '5') return 0.6;
                    if(c == '6') return 0.6;
                    if(c == '7') return 0.6;
                    if(c == '8') return 0.6;
                    if(c == '9') return 0.6;
                    if(c == '.') return 0.25;
                    if(c == ',') return 0.25;
                    if(c == '!') return 0.35;
                    if(c == '?') return 0.45;
                    if(c == ':') return 0.25;
                    if(c == ';') return 0.25;
                    if(c == '-') return 0.4;
                    if(c == '_') return 0.4;
                    if(c == '"') return 0.5;
                    if(c == '\'')return 0.3;
                    if(c == '%') return 0.85;
                    if(c == '&') return 0.85;
                    if(c == '/') return 0.45;
                    if(c == '\\')return 0.45;
                    if(c == '(') return 0.35;
                    if(c == ')') return 0.35;
                    if(c == '[') return 0.35;
                    if(c == ']') return 0.35;
                    if(c == '{') return 0.35;
                    if(c == '}') return 0.35;
                    if(c == '=') return 0.7;
                    if(c == '#') return 0.6;
                    if(c == '+') return 0.7;
                    if(c == '*') return 0.45;
                    if(c == '~') return 0.7;
                    if(c == '<') return 0.7;
                    if(c == '>') return 0.7;
                    if(c == '|') return 0.35;
                    if(c == '^') return 0.7;
                    if(c == '\u00df') return 0.65; // ß
                    if(c == '\u00e4') return 0.55; // ä
                    if(c == '\u00f6') return 0.6; // ö
                    if(c == '\u00fc') return 0.6; // ü
                    if(c == '\u00C4') return 0.7; // Ä
                    if(c == '\u00d6') return 0.75; // Ö
                    if(c == '\u00dc') return 0.7; // Ü
                    if(c == '\u00b0') return 0.4; // °
                    return 0.5;
                }

                if(c == ' ') return 0.25;
                if(c == 'a') return 0.5;
                if(c == 'b') return 0.6;
                if(c == 'c') return 0.45;
                if(c == 'd') return 0.58;
                if(c == 'e') return 0.5;
                if(c == 'f') return 0.3;
                if(c == 'g') return 0.6;
                if(c == 'h') return 0.55;
                if(c == 'i') return 0.25;
                if(c == 'j') return 0.25;
                if(c == 'k') return 0.5;
                if(c == 'l') return 0.25;
                if(c == 'm') return 0.85;
                if(c == 'n') return 0.55;
                if(c == 'o') return 0.6;
                if(c == 'p') return 0.6;
                if(c == 'q') return 0.6;
                if(c == 'r') return 0.35;
                if(c == 's') return 0.4;
                if(c == 't') return 0.35;
                if(c == 'u') return 0.55;
                if(c == 'v') return 0.5;
                if(c == 'w') return 0.7;
                if(c == 'x') return 0.45;
                if(c == 'y') return 0.5;
                if(c == 'z') return 0.45;
                if(c == 'A') return 0.65;
                if(c == 'B') return 0.55;
                if(c == 'C') return 0.6;
                if(c == 'D') return 0.7;
                if(c == 'E') return 0.5;
                if(c == 'F') return 0.5;
                if(c == 'G') return 0.7;
                if(c == 'H') return 0.7;
                if(c == 'I') return 0.25;
                if(c == 'J') return 0.35;
                if(c == 'K') return 0.6;
                if(c == 'L') return 0.45;
                if(c == 'M') return 0.9;
                if(c == 'N') return 0.75;
                if(c == 'O') return 0.75;
                if(c == 'P') return 0.55;
                if(c == 'Q') return 0.75;
                if(c == 'R') return 0.6;
                if(c == 'S') return 0.55;
                if(c == 'T') return 0.5;
                if(c == 'U') return 0.7;
                if(c == 'V') return 0.6;
                if(c == 'X') return 0.6;
                if(c == 'Y') return 0.55;
                if(c == 'Z') return 0.55;
                if(c == '0') return 0.55;
                if(c == '1') return 0.55;
                if(c == '2') return 0.55;
                if(c == '3') return 0.55;
                if(c == '4') return 0.55;
                if(c == '5') return 0.55;
                if(c == '6') return 0.55;
                if(c == '7') return 0.55;
                if(c == '8') return 0.55;
                if(c == '9') return 0.55;
                if(c == '.') return 0.2;
                if(c == ',') return 0.2;
                if(c == '!') return 0.3;
                if(c == '?') return 0.45;
                if(c == ':') return 0.2;
                if(c == ';') return 0.2;
                if(c == '-') return 0.4;
                if(c == '_') return 0.4;
                if(c == '"') return 0.4;
                if(c == '\'') return 0.25;
                if(c == '%') return 0.8;
                if(c == '&') return 0.8;
                if(c == '/') return 0.4;
                if(c == '\\') return 0.4;
                if(c == '(') return 0.3;
                if(c == ')') return 0.3;
                if(c == '[') return 0.3;
                if(c == ']') return 0.3;
                if(c == '{') return 0.3;
                if(c == '}') return 0.3;
                if(c == '=') return 0.7;
                if(c == '#') return 0.6;
                if(c == '+') return 0.7;
                if(c == '*') return 0.4;
                if(c == '~') return 0.7;
                if(c == '<') return 0.7;
                if(c == '>') return 0.7;
                if(c == '|') return 0.25;
                if(c == '^') return 0.7;
                if(c == '\u00df') return 0.55; // ß
                if(c == '\u00e4') return 0.5; // ä
                if(c == '\u00f6') return 0.6; // ö
                if(c == '\u00fc') return 0.55; // ü
                if(c == '\u00C4') return 0.65; // Ä
                if(c == '\u00d6') return 0.75; // Ö
                if(c == '\u00dc') return 0.7; // Ü
                if(c == '\u00b0') return 0.4; // °
                return 0.5;
            }
        };
    }



    private static final int numberOfLines(String string) {
        int count = 1;
        while(string.contains("\n")) {
            string = string.replaceFirst("\n", "");
            count++;
        }
        return count;
    }
}
