package cc.unknown.event.impl.render;

import cc.unknown.event.Event;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;

public class RenderItemEvent extends Event {

    /** The action type associated with rendering the item. */
    private EnumAction enumAction;

    /** Indicates whether the item is being used. */
    private boolean useItem;

    /** The progression of the animation associated with rendering the item. */
    private float animationProgression;

    /** The partial tick value used in rendering the item. */
    private float partialTicks;

    /** The swing progress of the item's animation. */
    private float swingProgress;

    /** The item to be rendered. */
    private ItemStack itemToRender;

    /**
     * Constructs a new {@code RenderItemEvent} with the specified parameters.
     *
     * @param enumAction the action type associated with rendering the item
     * @param useItem indicates whether the item is being used
     * @param animationProgression the progression of the animation associated with rendering the item
     * @param partialTicks the partial tick value used in rendering the item
     * @param swingProgress the swing progress of the item's animation
     * @param itemToRender the item to be rendered
     */
    public RenderItemEvent(EnumAction enumAction, boolean useItem, float animationProgression, float partialTicks, float swingProgress, ItemStack itemToRender) {
        this.enumAction = enumAction;
        this.useItem = useItem;
        this.animationProgression = animationProgression;
        this.partialTicks = partialTicks;
        this.swingProgress = swingProgress;
        this.itemToRender = itemToRender;
    }

    /**
     * Returns the action type associated with rendering the item.
     *
     * @return the action type associated with rendering the item
     */
    public EnumAction getEnumAction() {
        return enumAction;
    }

    /**
     * Sets the action type associated with rendering the item.
     *
     * @param enumAction the action type to set
     */
    public void setEnumAction(EnumAction enumAction) {
        this.enumAction = enumAction;
    }

    /**
     * Returns true if the item is being used, false otherwise.
     *
     * @return true if the item is being used, false otherwise
     */
    public boolean isUseItem() {
        return useItem;
    }

    /**
     * Sets whether the item is being used.
     *
     * @param useItem true if the item is being used, false otherwise
     */
    public void setUseItem(boolean useItem) {
        this.useItem = useItem;
    }

    /**
     * Returns the progression of the animation associated with rendering the item.
     *
     * @return the progression of the animation associated with rendering the item
     */
    public float getAnimationProgression() {
        return animationProgression;
    }

    /**
     * Sets the progression of the animation associated with rendering the item.
     *
     * @param animationProgression the progression of the animation to set
     */
    public void setAnimationProgression(float animationProgression) {
        this.animationProgression = animationProgression;
    }

    /**
     * Returns the partial tick value used in rendering the item.
     *
     * @return the partial tick value used in rendering the item
     */
    public float getPartialTicks() {
        return partialTicks;
    }

    /**
     * Sets the partial tick value used in rendering the item.
     *
     * @param partialTicks the partial tick value to set
     */
    public void setPartialTicks(float partialTicks) {
        this.partialTicks = partialTicks;
    }

    /**
     * Returns the swing progress of the item's animation.
     *
     * @return the swing progress of the item's animation
     */
    public float getSwingProgress() {
        return swingProgress;
    }

    /**
     * Sets the swing progress of the item's animation.
     *
     * @param swingProgress the swing progress to set
     */
    public void setSwingProgress(float swingProgress) {
        this.swingProgress = swingProgress;
    }

    /**
     * Returns the item to be rendered.
     *
     * @return the item to be rendered
     */
    public ItemStack getItemToRender() {
        return itemToRender;
    }

    /**
     * Sets the item to be rendered.
     *
     * @param itemToRender the item to be rendered
     */
    public void setItemToRender(ItemStack itemToRender) {
        this.itemToRender = itemToRender;
    }
}