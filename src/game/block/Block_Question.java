package game.block;

import embgine.core.loaders.BlockLoader;
import embgine.graphics.Texture;
import game.master.MarioMaster;

public class Block_Question extends BlockLoader{

	public Block_Question() {
		super(true, new Texture("game/textures/block_question.png", 2), MarioMaster.LAYER_GAME, false);
	}

}
