package com.github.rccookie.greenfoot.ui;

import java.util.List;

import com.github.rccookie.greenfoot.core.Color;
import com.github.rccookie.greenfoot.core.User;

public class Scoreboard extends UIPanel {
    public static final int MIN_WIDTH = 200, MIN_HEIGHT = (int) (ScorePanel.HEIGHT * 2.5);

    public Scoreboard(int width, int height) {
        super(
            width < MIN_WIDTH ? (width = MIN_WIDTH) : width,
            height < MIN_HEIGHT ? (height = MIN_HEIGHT) : height,
            (Color)null
        );
        try{
            final int numberOfScores = height / ScorePanel.HEIGHT;

            final List<User> topScores = User.getTop(numberOfScores);
            if(containsUser(topScores)) {
                int index = 0;
                for(final User user : topScores) {
                    add(getScorePanelFor(width, index, user), 0.5, 0, 0, (int)(ScorePanel.HEIGHT * (index + 0.5)));
                    index++;
                }
            }
            else {
                int index = 0;
                for(final User user : User.getTop(numberOfScores - 1)) {
                    add(getScorePanelFor(width, index, user), 0.5, 0, 0, (int)(ScorePanel.HEIGHT * (index + 0.5)));
                    index++;
                }
                add(getScorePanelFor(width, index, User.getOrGuest()), 0.5, 1, 0, -ScorePanel.HEIGHT / 2);
            }
        }
        catch(Exception e) {
            add(new Text("Failed to load highscores"), 0.5, 0.5);
        }
    }



    public ScorePanel getScorePanelFor(final int width, final int index, final User user) {
        return new ScorePanel(width, user, index);
    }



    private static final boolean containsUser(List<User> users) {
        String name = User.getOrGuest().getName();
        for(User user : users) {
            if(user.getName().equals(name)) return true;
        }
        return false;
    }


    public static class ScorePanel extends UIPanel {

        public static int SCORE_PRECITION = 1;

        static final Color[] COLORS = {new Color(200, 200, 200), new Color(220, 220, 220)};
        static final Color PLAYER_COLOR = Color.RED;
        static final int HEIGHT = 40;

        public ScorePanel(int x, User user, int index) {
            super(x, HEIGHT, COLORS[index % 2]);

            Text text = new Text("#" + user.getRank()).setSize(18);
            if(user.getName().equals(User.getOrGuest().getName()))
                text.setColor(PLAYER_COLOR);
            add(text, 0.05, 0.5);


            text = new Text();
            text.setImage(user.getImage());
            text.getImage().scale(36, 36);
            add(text, 0.1, 0.5, 20, 0);

            text = new Text(user.getName()).setSize(16).setColor(getColor(user));
            add(text, 0.2, 0.5, text.getImage().getWidth() / 2, 0);

            text = new Text("Score: " + user.getScore() / (double)SCORE_PRECITION).setSize(16).setColor(getColor(user));
            add(text, 0.85, 0.5, text.getImage().getWidth() / 2, 0);

            addAdditionalInfo(getColor(user));
        }

        private Color getColor(User user) {
            return user.getName().equals(User.getOrGuest().getName()) ? Color.RED : Color.DARK_GRAY;
        }

        protected void addAdditionalInfo(Color color) {}
    }
}