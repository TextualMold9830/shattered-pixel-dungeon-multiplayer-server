package com.nikita22007.multiplayer.utils.text;

import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import org.jetbrains.annotations.CheckReturnValue;
import org.jetbrains.annotations.Contract;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;

public class LocalizedString {
    
    public static LocalizedString EMPTY = LocalizedString.raw("");
    
    public enum Mode {
        KEY,
        RAW,
        TRANSFORM,
        CONCAT
    }

    public enum Transform {
        CAPITALIZE,
        TITLE_CASE,
        UPPER_CASE,
        LOWER_CASE
    }

    private final Mode mode;
    private final LocalizedKey key;
    private final String raw;
    private final Object[] args;
    private final Transform transform;
    private final LocalizedString text;
    private final Object[] parts;

    private LocalizedString(Mode mode, LocalizedKey key, String raw, Object[] args, Transform transform, LocalizedString text, Object[] parts) {
        this.mode = mode;
        this.key = key;
        this.raw = raw;
        this.args = args == null ? new Object[0] : args;
        this.transform = transform;
        this.text = text;
        this.parts = parts == null ? new Object[0] : parts;
    }

    @CheckReturnValue
    public static LocalizedString key(LocalizedKey key, Object... args) {
        return new LocalizedString(Mode.KEY, key, null, args, null, null, null);
    }

    @CheckReturnValue
    public static LocalizedString raw(String raw, Object... args) {
        return new LocalizedString(Mode.RAW, null, raw, args, null, null, null);
    }

    @CheckReturnValue
    public static LocalizedString[] raw(String[] options) {
        LocalizedString[] localizedStrings = new LocalizedString[options.length];
        for (int i = 0; i < options.length; i++) {
            localizedStrings[i] = LocalizedString.raw(options[i]);
        }
        return localizedStrings;
    }

    @CheckReturnValue
    public static LocalizedString transform(Transform transform, LocalizedString text) {
        return new LocalizedString(Mode.TRANSFORM, null, null, null, transform, text, null);
    }

    @CheckReturnValue
    public static LocalizedString concat(Object... parts) {
        ArrayList<Object> flattened = new ArrayList<>();
        flattenConcatParts(flattened, parts);
        return new LocalizedString(Mode.CONCAT, null, null, null, null, null, flattened.toArray(new Object[0]));
    }

    private static void flattenConcatParts(ArrayList<Object> flattened, Object[] parts) {
        if (parts == null) {
            return;
        }
        for (Object part : parts) {
            if (part instanceof LocalizedString && ((LocalizedString) part).mode() == Mode.CONCAT) {
                flattenConcatParts(flattened, ((LocalizedString) part).parts());
            } else {
                flattened.add(part);
            }
        }
    }

    @CheckReturnValue
    public Mode mode() {
        return mode;
    }

    @CheckReturnValue
    public LocalizedKey key() {
        return key;
    }

    @CheckReturnValue
    public String raw() {
        return raw;
    }

    @CheckReturnValue
    public Object[] args() {
        return args;
    }

    @CheckReturnValue
    public Transform transform() {
        return transform;
    }

    @CheckReturnValue
    public LocalizedString text() {
        return text;
    }

    @CheckReturnValue
    public Object[] parts() {
        return parts;
    }

    @CheckReturnValue
    public static String[] resolveArray(LocalizedString[] localizedStrings) {
        String[] strings = new String[localizedStrings.length];
        for (int i= 0 ; i < localizedStrings.length; i++) {
            if (localizedStrings[i] != null) {
                strings[i] = localizedStrings[i].toString();
            }
        }
        return strings;
    }

    @CheckReturnValue
    public LocalizedString toUpperCase(Locale locale) {
        return Messages.toUpperCase(this, locale);
    }

    @Override
    public String toString() {
        return Messages.resolve(this);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof LocalizedString)) {
            return false;
        }
        LocalizedString other = (LocalizedString) obj;
        return mode == other.mode
                && Objects.equals(key, other.key)
                && Objects.equals(raw, other.raw)
                && Arrays.equals(args, other.args)
                && transform == other.transform
                && Objects.equals(text, other.text)
                && Arrays.equals(parts, other.parts);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(mode, key, raw, transform, text);
        result = 31 * result + Arrays.hashCode(args);
        result = 31 * result + Arrays.hashCode(parts);
        return result;
    }
}
