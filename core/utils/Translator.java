package forestry.core.utils;

import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

//TODO - sides issues
public class Translator {
	private Translator() {

	}

	@SuppressWarnings("deprecation")
	public static String translateToLocal(String key) {
		return translateToLocalFormatted(key);
	}

	@SuppressWarnings("deprecation")
	public static boolean canTranslateToLocal(String key) {
		return I18n.hasKey(key);
	}

	public static String translateToLocalFormatted(String key, Object... format) {
		return I18n.format(key, format);
	}
}
