package com.production.player;

import com.production.models.Game;

public abstract class Player {

	public abstract boolean isHuman();

	public abstract void updateGame(Game game);
}
