package cc.unknown.command.commands;

import cc.unknown.command.Command;
import cc.unknown.command.Flips;
import cc.unknown.utils.keystrokes.KeyStrokes;

@Flips(name = "Key", alias = "key", desc = "Show the all commands", syntax = ".help")
public class KeyStrokesCommand extends Command {

	@Override
	public void onExecute(String[] args) {
		KeyStrokes.isConfigGui();
	}
}
