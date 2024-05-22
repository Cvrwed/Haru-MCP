package cc.unknown.utils.keystrokes;

public enum KeyStroke {
	instance;
    private int xPosition;
    private int yPosition;
    private int colorIndex;
    private boolean displayMouseButtons;
    private boolean isEnabled;
    private boolean displayOutline;

    KeyStroke() {
        xPosition = 0;
        yPosition = 0;
        colorIndex = 0;
        displayMouseButtons = false;
        isEnabled = true;
        displayOutline = false;
    }

    public int getXPosition() {
        return xPosition;
    }

    public void setXPosition(int xPosition) {
        this.xPosition = xPosition;
    }

    public int getYPosition() {
        return yPosition;
    }

    public void setYPosition(int yPosition) {
        this.yPosition = yPosition;
    }

    public int getColorIndex() {
        return colorIndex;
    }

    public void setColorIndex(int colorIndex) {
        this.colorIndex = colorIndex;
    }

    public boolean isDisplayMouseButtons() {
        return displayMouseButtons;
    }

    public void setDisplayMouseButtons(boolean displayMouseButtons) {
        this.displayMouseButtons = displayMouseButtons;
    }

    public boolean isIsEnabled() {
        return isEnabled;
    }

    public void setIsEnabled(boolean isEnabled) {
        this.isEnabled = isEnabled;
    }

    public boolean isDisplayOutline() {
        return displayOutline;
    }

    public void setDisplayOutline(boolean displayOutline) {
        this.displayOutline = displayOutline;
    }
}