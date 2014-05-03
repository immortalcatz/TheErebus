package erebus.world.biomes.decorators;
import net.minecraft.block.Block;
import net.minecraft.world.gen.feature.WorldGenLakes;
import erebus.ModBlocks;
import erebus.world.biomes.decorators.type.FeatureType;
import erebus.world.biomes.decorators.type.OreSettings;
import erebus.world.biomes.decorators.type.OreSettings.OreType;
import erebus.world.feature.decoration.WorldGenScorchedWood;
import erebus.world.feature.structure.WorldGenAntlionLair;

public class BiomeDecoratorVolcanicDesert extends BiomeDecoratorBaseErebus{
	private final WorldGenAntlionLair genAntlionLair = new WorldGenAntlionLair();
	private final WorldGenLakes genLavaLakes = new WorldGenLakes(Block.lavaMoving.blockID);
	private final WorldGenScorchedWood genScorchedWood = new WorldGenScorchedWood();
	
	@Override
	public void populate(){
		for(attempt = 0; attempt < 35; attempt++){
			xx = x+offsetXZ();
			yy = 15+rand.nextInt(90);
			zz = z+offsetXZ();
			
			if (world.getBlockId(xx,yy-1,zz) == Block.sand.blockID && world.isAirBlock(xx,yy,zz)){
				genLavaLakes.generate(world,world.rand,xx,yy,zz);
			}
		}
	}
	
	@Override
	public void decorate(){
		for(attempt = 0; attempt < 10; attempt++){
			xx = x+offsetXZ();
			yy = rand.nextInt(120);
			zz = z+offsetXZ();
			
			if (world.getBlockId(xx,yy,zz) == ModBlocks.umberstone.blockID && world.isAirBlock(xx,yy-1,zz)){
				world.setBlock(xx,yy,zz,Block.lavaMoving.blockID);
				world.scheduledUpdatesAreImmediate = true;
				Block.blocksList[Block.lavaMoving.blockID].updateTick(world,xx,yy,zz,rand);
				world.scheduledUpdatesAreImmediate = false;
			}
		}

		for(attempt = 0; attempt < 22; attempt++){
			xx = x+offsetXZ();
			yy = rand.nextInt(120);
			zz = z+offsetXZ();
			
			if (world.getBlockId(xx,yy-1,zz) == Block.sand.blockID && world.isAirBlock(xx,yy,zz) && !world.isAirBlock(xx,yy-2,zz)){
				genScorchedWood.generate(world,rand,xx,yy,zz);
				if (rand.nextInt(4)!=0)break;
			}
		}

		if (rand.nextInt(34) == 0){
			for(int attempt = 0; attempt < 15; attempt++){
				if (genAntlionLair.generate(world,rand,x+5+rand.nextInt(6)+8,15+rand.nextInt(35),z+5+rand.nextInt(6)+8))break;
			}
		}
	}
	
	@Override
	public void generateFeature(FeatureType featureType){
		if (featureType == FeatureType.REDGEM){
			for(attempt = 0; attempt < 10; attempt++){
				genRedGem.generate(world,rand,x+offsetXZ(),rand.nextInt(64),z+offsetXZ());
			}
		}
		else super.generateFeature(featureType);
	}
	
	@Override
	@SuppressWarnings("incomplete-switch")
	protected void modifyOreGen(OreSettings oreGen, OreType oreType, boolean extraOres){
		switch(oreType){
			case COAL: oreGen.setChance(0F); break;
			case GOLD: oreGen.setIterations(extraOres?1:2,extraOres?2:3); break; // less common
			case DIAMOND: oreGen.setChance(0.6F).setIterations(1).setOreAmount(4).setY(5,16); break; // clusters of 4, ~7 times smaller area thus lowered chance and iterations // TODO special ore
			case JADE: oreGen.setIterations(0,2); break; // less common
			case FOSSIL: oreGen.setChance(0.04F).setIterations(0,1); break; // much more rare
			
		}
	}
}