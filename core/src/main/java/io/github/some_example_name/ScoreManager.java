package io.github.some_example_name;

public class ScoreManager {
    private int playerScore;
    private int aiScore;
    private final int maxScore; // puntos para ganar

    public ScoreManager(int maxScore) {
        this.maxScore    = maxScore;
        this.playerScore = 0;
        this.aiScore     = 0;
    }

    public void playerScores() { playerScore++; }
    public void aiScores()     { aiScore++;     }

    public int getPlayerScore() { return playerScore; }
    public int getAiScore()     { return aiScore;     }

    public boolean playerWins() { return playerScore >= maxScore; }
    public boolean aiWins()     { return aiScore     >= maxScore; }

    public void reset() {
        playerScore = 0;
        aiScore     = 0;
    }
}
