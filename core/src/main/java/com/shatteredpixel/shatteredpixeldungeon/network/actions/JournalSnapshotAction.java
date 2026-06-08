package com.shatteredpixel.shatteredpixeldungeon.network.actions;

import org.jetbrains.annotations.NotNull;

public class JournalSnapshotAction implements LiveStateNetworkAction {
	@Override
	public @NotNull String actionName() {
		return "journal_snapshot";
	}
}
