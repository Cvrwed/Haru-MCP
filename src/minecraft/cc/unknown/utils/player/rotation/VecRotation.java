package cc.unknown.utils.player.rotation;

import net.minecraft.util.enums.EnumFacing;
import net.minecraft.util.vec.Vec3;

public class VecRotation {
	private Vec3 vec;
	private Rotation rotation;
	private EnumFacing sideHit;

	public VecRotation(final Vec3 vec, final Rotation rotation) {
		this.setVec(vec);
		this.setRotation(rotation);
	}

	public VecRotation(final Vec3 vec, final Rotation rot, final EnumFacing sideHit) {
		this(vec, rot);
		this.setSideHit(sideHit);
	}

	public Vec3 getVec() {
		return this.vec;
	}

	public void setVec(final Vec3 vec) {
		this.vec = vec;
	}

	public Rotation getRotation() {
		return this.rotation;
	}

	public void setRotation(final Rotation rotation) {
		this.rotation = rotation;
	}

	public EnumFacing getSideHit() {
		return this.sideHit;
	}

	public void setSideHit(final EnumFacing sideHit) {
		this.sideHit = sideHit;
	}
}
