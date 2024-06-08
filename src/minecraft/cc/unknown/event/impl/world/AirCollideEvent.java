package cc.unknown.event.impl.world;

import cc.unknown.event.Event;
import net.minecraft.block.BlockAir;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
import net.minecraft.util.vec.AxisAlignedBB;
import net.minecraft.world.World;

public class AirCollideEvent extends Event {

    /** The boundingBox indicating the collision area. */
    private AxisAlignedBB boundingBox;

    /** The world in which the collision occurs. */
    private World worldIn;

    /** The position of the collision. */
    private BlockPos pos;

    /** The block state at the collision position. */
    private IBlockState state;

    /** The minimum x-coordinate of the collision area. */
    private double minX;

    /** The minimum y-coordinate of the collision area. */
    private double minY;

    /** The minimum z-coordinate of the collision area. */
    private double minZ;

    /** The maximum x-coordinate of the collision area. */
    private double maxX;

    /** The maximum y-coordinate of the collision area. */
    private double maxY;

    /** The maximum z-coordinate of the collision area. */
    private double maxZ;

    /**
     * Constructs a new {@code AirCollideEvent} with the specified parameters.
     *
     * @param worldIn the world in which the collision occurs
     * @param pos the position of the collision
     * @param state the block state at the collision position
     * @param minX the minimum x-coordinate of the collision area
     * @param minY the minimum y-coordinate of the collision area
     * @param minZ the minimum z-coordinate of the collision area
     * @param maxX the maximum x-coordinate of the collision area
     * @param maxY the maximum y-coordinate of the collision area
     * @param maxZ the maximum z-coordinate of the collision area
     */
    public AirCollideEvent(World worldIn, BlockPos pos, IBlockState state, double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
        this.worldIn = worldIn;
        this.pos = pos;
        this.state = state;
        this.minX = minX;
        this.minY = minY;
        this.minZ = minZ;
        this.maxX = maxX;
        this.maxY = maxY;
        this.maxZ = maxZ;
    }

    /**
     * Returns the return value indicating the collision area.
     *
     * @return the collision area
     */
    public AxisAlignedBB getBoundingBox() {
        return boundingBox;
    }

    /**
     * Sets the return value indicating the collision area.
     *
     * @param returnValue the collision area to set
     */
    public void setBoundingBox(AxisAlignedBB boundingBox) {
        this.boundingBox = boundingBox;
    }

    /**
     * Returns the world in which the collision occurs.
     *
     * @return the world in which the collision occurs
     */
    public World getWorldIn() {
        return worldIn;
    }

    /**
     * Sets the world in which the collision occurs.
     *
     * @param worldIn the world to set
     */
    public void setWorldIn(World worldIn) {
        this.worldIn = worldIn;
    }

    /**
     * Returns the position of the collision.
     *
     * @return the position of the collision
     */
    public BlockPos getPos() {
        return pos;
    }

    /**
     * Sets the position of the collision.
     *
     * @param pos the position to set
     */
    public void setPos(BlockPos pos) {
        this.pos = pos;
    }

    /**
     * Returns the block state at the collision position.
     *
     * @return the block state at the collision position
     */
    public IBlockState getState() {
        return state;
    }

    /**
     * Sets the block state at the collision position.
     *
     * @param state the block state to set
     */
    public void setState(IBlockState state) {
        this.state = state;
    }

    /**
     * Returns the minimum x-coordinate of the collision area.
     *
     * @return the minimum x-coordinate of the collision area
     */
    public double getMinX() {
        return minX;
    }

    /**
     * Sets the minimum x-coordinate of the collision area.
     *
     * @param minX the minimum x-coordinate to set
     */
    public void setMinX(double minX) {
        this.minX = minX;
    }

    /**
     * Returns the minimum y-coordinate of the collision area.
     *
     * @return the minimum y-coordinate of the collision area
     */
    public double getMinY() {
        return minY;
    }

    /**
     * Sets the minimum y-coordinate of the collision area.
     *
     * @param minY the minimum y-coordinate to set
     */
    public void setMinY(double minY) {
        this.minY = minY;
    }

    /**
     * Returns the minimum z-coordinate of the collision area.
     *
     * @return the minimum z-coordinate of the collision area
     */
    public double getMinZ() {
        return minZ;
    }

    /**
     * Sets the minimum z-coordinate of the collision area.
     *
     * @param minZ the minimum z-coordinate to set
     */
    public void setMinZ(double minZ) {
        this.minZ = minZ;
    }

    /**
     * Returns the maximum x-coordinate of the collision area.
     *
     * @return the maximum x-coordinate of the collision area
     */
    public double getMaxX() {
        return maxX;
    }

    /**
     * Sets the maximum x-coordinate of the collision area.
     *
     * @param maxX the maximum x-coordinate to set
     */
    public void setMaxX(double maxX) {
        this.maxX = maxX;
    }

    /**
     * Returns the maximum y-coordinate of the collision area.
     *
     * @return the maximum y-coordinate of the collision area
     */
    public double getMaxY() {
        return maxY;
    }

    /**
     * Sets the maximum y-coordinate of the collision area.
     *
     * @param maxY the maximum y-coordinate to set
     */
    public void setMaxY(double maxY) {
        this.maxY = maxY;
    }

    /**
     * Returns the maximum z-coordinate of the collision area.
     *
     * @return the maximum z-coordinate of the collision area
     */
    public double getMaxZ() {
        return maxZ;
    }

    /**
     * Sets the maximum z-coordinate of the collision area.
     *
     * @param maxZ the maximum z-coordinate to set
     */
	public void setMaxZ(double maxZ) {
		this.maxZ = maxZ;
	}
}
