package com.production.player;

import com.production.models.Game;

public class HumanPlayer extends Player {

	@Override
	public boolean isHuman() {
		return true;
	}

	@Override
	public void updateGame(Game game) {}
}
