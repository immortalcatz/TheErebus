package erebus.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import erebus.tileentity.TileEntityKitchenCounter;

public class ContainerKitchenCounter extends Container{
	
	protected TileEntityKitchenCounter counter;

	public ContainerKitchenCounter(InventoryPlayer inventory, TileEntityKitchenCounter tileentity) {
		counter = tileentity;
		
		for(int q = 0; q < 3; q++){
			for(int w = 0; w < 3; w++){
				addSlotToContainer(new Slot(tileentity, w + q * 3, 62 + w * 18, 17 + q * 18));
			}
		}
		
		bindPlayerInventory(inventory);
	}
	
	protected void bindPlayerInventory(InventoryPlayer invPlayer){
		for(int row = 0; row < 3; row++){
			for(int column = 0; column < 9; column++){
				addSlotToContainer(new Slot(invPlayer, column + row * 9 + 9, 8 + column * 18, 84 + row * 18));
			}
		}
		
		for(int slotNumber = 0; slotNumber < 9; slotNumber++){
			addSlotToContainer(new Slot(invPlayer, slotNumber, 8 + slotNumber * 18, 142));
		}
	}
	
	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int slot){
		ItemStack stack = null;
		Slot slotObj = (Slot) inventorySlots.get(slot);
		
		if(slotObj != null && slotObj.getHasStack()){
			ItemStack stackInSlot = slotObj.getStack();
			stack = stackInSlot.copy();
			
			if(slot < 9){
				if(!this.mergeItemStack(stackInSlot, 0, 35, true)){
					return null;
				}
			} else if(!this.mergeItemStack(stackInSlot, 0, 9, false)){
				return null;
			}
			
			if(stackInSlot.stackSize == 0){
				slotObj.putStack(null);
			} else {
				slotObj.onSlotChanged();
			}
			
			if(stackInSlot.stackSize == stack.stackSize){
				return null;
			}
			
			slotObj.onPickupFromSlot(player, stackInSlot);
		}
		return stack;
	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return counter.isUseableByPlayer(player);
	}

}
