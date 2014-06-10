package erebus.item;

import net.minecraft.block.Block;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBucket;
import net.minecraft.item.ItemStack;

public class BucketOfHoney extends ItemBucket {

	public BucketOfHoney(Block full) {
		super(full);
		setMaxStackSize(1);
	}

	@Override
	public boolean hasContainerItem(ItemStack stack) {
		return true;
	}

	@Override
	public Item getContainerItem() {
		return Items.bucket;
	}
}