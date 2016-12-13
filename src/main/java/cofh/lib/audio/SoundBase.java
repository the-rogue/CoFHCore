
package cofh.lib.audio;

import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.Sound;
import net.minecraft.client.audio.SoundEventAccessor;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;


/**
 * Generic ISound class with lots of constructor functionality. Required because - of course - Mojang has no generic that lets you specify *any* arguments for this.
 *
 * @author skyboy
 *
 */
@SideOnly(Side.CLIENT)
public class SoundBase implements ISound
{

	protected Sound sound;
	protected SoundCategory soundcategory;
    private SoundEventAccessor soundEvent;
	protected AttenuationType attenuation;
	protected final ResourceLocation soundlocation;
	protected float volume;
	protected float pitch;
	protected float x;
	protected float y;
	protected float z;
	protected boolean repeat;
	protected int repeatDelay;

	public SoundBase(String sound, SoundCategory category)
	{

		this(sound, 0, category);
	}

	public SoundBase(String sound, float volume, SoundCategory category)
	{

		this(sound, volume, 0, category);
	}

	public SoundBase(String sound, float volume, float pitch, SoundCategory category)
	{

		this(sound, volume, pitch, false, 0, category);
	}

	public SoundBase(String sound, float volume, float pitch, boolean repeat, int repeatDelay, SoundCategory category)
	{

		this(sound, volume, pitch, repeat, repeatDelay, 0, 0, 0, AttenuationType.NONE, category);
	}

	public SoundBase(String sound, float volume, float pitch, double x, double y, double z, SoundCategory category)
	{

		this(sound, volume, pitch, false, 0, x, y, z, category);
	}

	public SoundBase(String sound, float volume, float pitch, boolean repeat, int repeatDelay, double x, double y, double z, SoundCategory category)
	{

		this(sound, volume, pitch, repeat, repeatDelay, x, y, z, AttenuationType.LINEAR, category);
	}

	public SoundBase(String sound, float volume, float pitch, boolean repeat, int repeatDelay, double x, double y, double z, AttenuationType attenuation, SoundCategory category)
	{

		this(new ResourceLocation(sound), volume, pitch, repeat, repeatDelay, x, y, z, attenuation, category);
	}

	public SoundBase(ResourceLocation sound, SoundCategory category)
	{

		this(sound, 0, category);
	}

	public SoundBase(ResourceLocation sound, float volume, SoundCategory category)
	{

		this(sound, volume, 0, category);
	}

	public SoundBase(ResourceLocation sound, float volume, float pitch, SoundCategory category)
	{

		this(sound, volume, pitch, false, 0, category);
	}

	public SoundBase(ResourceLocation sound, float volume, float pitch, boolean repeat, int repeatDelay, SoundCategory category)
	{

		this(sound, volume, pitch, repeat, repeatDelay, 0, 0, 0, AttenuationType.NONE, category);
	}

	public SoundBase(ResourceLocation sound, float volume, float pitch, double x, double y, double z, SoundCategory category)
	{

		this(sound, volume, pitch, false, 0, x, y, z, category);
	}

	public SoundBase(ResourceLocation sound, float volume, float pitch, boolean repeat, int repeatDelay, double x, double y, double z, SoundCategory category)
	{

		this(sound, volume, pitch, repeat, repeatDelay, x, y, z, AttenuationType.LINEAR, category);
	}

	public SoundBase(ResourceLocation sound, float volume, float pitch, boolean repeat, int repeatDelay, double x, double y, double z, AttenuationType attenuation, SoundCategory category)
	{

		this.attenuation = attenuation;
		this.soundlocation = sound;
		this.volume = volume;
		this.pitch = pitch;
		this.x = (float) x;
		this.y = (float) y;
		this.z = (float) z;
		this.repeat = repeat;
		this.repeatDelay = repeatDelay;
		this.soundcategory = category;
	}

	public SoundBase(SoundBase other)
	{

		this.sound = other.sound;
		this.attenuation = other.attenuation;
		this.soundlocation = other.soundlocation;
		this.volume = other.volume;
		this.pitch = other.pitch;
		this.x = other.x;
		this.y = other.y;
		this.z = other.z;
		this.repeat = other.repeat;
		this.repeatDelay = other.repeatDelay;
		this.soundcategory = other.soundcategory;
	}

	@Override
	public AttenuationType getAttenuationType()
	{

		return attenuation;
	}

	@Override
	public float getVolume()
	{

		return volume;
	}

	@Override
	public float getPitch()
	{

		return pitch;
	}

	@Override
	public float getXPosF()
	{

		return x;
	}

	@Override
	public float getYPosF()
	{

		return y;
	}

	@Override
	public float getZPosF()
	{

		return z;
	}

	@Override
	public boolean canRepeat()
	{

		return repeat;
	}

	@Override
	public int getRepeatDelay()
	{

		return repeatDelay;
	}

	@Override
	public ResourceLocation getSoundLocation()
	{
		return soundlocation;
	}


    public SoundEventAccessor createAccessor(SoundHandler handler)
    {
        this.soundEvent = handler.getAccessor(this.soundlocation);

        if (this.soundEvent == null)
        {
            this.sound = SoundHandler.MISSING_SOUND;
        }
        else
        {
            this.sound = this.soundEvent.cloneEntry();
        }

        return this.soundEvent;
    }

	@Override
	public Sound getSound()
	{
		return sound;
	}

	@Override
	public SoundCategory getCategory()
	{
		return soundcategory;
	}

}
