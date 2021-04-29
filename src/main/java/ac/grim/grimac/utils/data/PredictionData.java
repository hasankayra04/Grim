package ac.grim.grimac.utils.data;

import ac.grim.grimac.GrimPlayer;
import ac.grim.grimac.utils.collisions.Collisions;
import net.minecraft.server.v1_16_R3.EntityBoat;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PredictionData {
    public GrimPlayer grimPlayer;
    public double playerX;
    public double playerY;
    public double playerZ;
    public float xRot;
    public float yRot;
    public boolean onGround;
    public boolean isSprinting;
    public boolean isSneaking;
    public boolean isFlying;
    public boolean isSwimming;
    public boolean isClimbing;
    public boolean isFallFlying;
    public World playerWorld;
    public WorldBorder playerWorldBorder;

    public double movementSpeed;
    public float jumpAmplifier;
    public float levitationAmplifier;
    public float flySpeed;

    public double fallDistance;

    // Debug, does nothing.
    public int number;

    public boolean inVehicle;
    public boolean boatUnderwater;
    public Entity playerVehicle;
    public float vehicleHorizontal;
    public float vehicleForward;

    // For regular movement
    public PredictionData(GrimPlayer grimPlayer, double playerX, double playerY, double playerZ, float xRot, float yRot, boolean onGround) {
        this.grimPlayer = grimPlayer;
        this.playerX = playerX;
        this.playerY = playerY;
        this.playerZ = playerZ;
        this.xRot = xRot;
        this.yRot = yRot;
        this.onGround = onGround;
        this.inVehicle = false;

        this.number = grimPlayer.taskNumber.getAndIncrement();

        this.isSprinting = grimPlayer.isPacketSprinting;
        this.isSneaking = grimPlayer.isPacketSneaking;

        this.isFlying = grimPlayer.packetIsFlying;

        this.isClimbing = Collisions.onClimbable(grimPlayer);
        this.isFallFlying = grimPlayer.bukkitPlayer.isGliding();
        this.playerWorld = grimPlayer.bukkitPlayer.getWorld();
        this.fallDistance = grimPlayer.bukkitPlayer.getFallDistance();
        this.movementSpeed = grimPlayer.bukkitPlayer.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).getValue();

        // When a player punches a mob, bukkit thinks the player isn't sprinting (?)
        // But they are, so we need to multiply by sprinting speed boost until I just get the player's attributes from packets
        if (isSprinting && !grimPlayer.bukkitPlayer.isSprinting()) this.movementSpeed *= 1.3;

        PotionEffect jumpEffect = grimPlayer.bukkitPlayer.getPotionEffect(PotionEffectType.JUMP);
        this.jumpAmplifier = jumpEffect == null ? 0 : jumpEffect.getAmplifier();

        PotionEffect levitationEffect = grimPlayer.bukkitPlayer.getPotionEffect(PotionEffectType.LEVITATION);
        this.levitationAmplifier = levitationEffect == null ? 0 : levitationEffect.getAmplifier();

        this.flySpeed = grimPlayer.entityPlayer.abilities.flySpeed;
        this.playerVehicle = grimPlayer.bukkitPlayer.getVehicle();
    }

    // For boat movement
    public PredictionData(GrimPlayer grimPlayer, double boatX, double boatY, double boatZ, float xRot, float yRot) {
        this.grimPlayer = grimPlayer;
        this.playerX = boatX;
        this.playerY = boatY;
        this.playerZ = boatZ;
        this.xRot = xRot;
        this.yRot = yRot;
        this.playerVehicle = grimPlayer.bukkitPlayer.getVehicle();
        this.vehicleForward = grimPlayer.packetVehicleForward;
        this.vehicleHorizontal = grimPlayer.packetVehicleHorizontal;

        this.boatUnderwater = false;
        this.inVehicle = true;
        if (grimPlayer.entityPlayer.getVehicle() instanceof EntityBoat) {
            EntityBoat boat = (EntityBoat) grimPlayer.entityPlayer.getVehicle();
            this.boatUnderwater = boat.aI();
        }

        this.isFlying = false;
        this.isSwimming = false;
        this.isClimbing = false;
        this.isFallFlying = false;
        this.playerWorld = grimPlayer.bukkitPlayer.getWorld();
        this.fallDistance = grimPlayer.bukkitPlayer.getFallDistance();
        this.movementSpeed = grimPlayer.bukkitPlayer.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).getValue();
    }
}
