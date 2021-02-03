package com.github.rccookie.common.data;

import java.io.Serializable;
import java.lang.reflect.Field;

/**
 * Saveables are used as a simple way to save objects using various data saving forms.
 * <p><b>Every Saveable implementation has to declare a static String {@code SAVE_DIR}</b>
 * that specifies the location for types of this object to be saved. The field can have
 * any access modifier and may be final.
 * <p>Every saveable can return the directory to save in specified by {@code getSaveDir()}.
 * The file name of each instance is specified by {@code getSaveName()}.
 */
public interface Saveable extends Serializable {

    /**
     * Returns a string representing the name of the objects save file (without the file ending).
     * <p>This method should return different names for different instances of the same class so
     * that they differ and create multiply save files.
     * 
     * @return The name of the save file
     */
    public default String getSaveName() {
        return getClass().getSimpleName().toLowerCase() + hashCode();
    }

    public default void setSaveName(String name) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("The class " + getClass().getSimpleName() + " does not implement a renaming system and thus does not support renaming.");
    }

    
    public static String getSaveDir(Saveable saveable) {
        return getSaveDir(saveable.getClass());
    }

    public static String getSaveDir(Class<? extends Saveable> cls) {
        try {
            Field saveDirField = cls.getDeclaredField("SAVE_DIR");
            saveDirField.trySetAccessible();
            return (String)saveDirField.get(null);
        } catch(Exception e) { }
        throw new MissingImplementationException(cls);
    }
}

final class MissingImplementationException extends RuntimeException {
	private static final long serialVersionUID = 1470101803521919580L;
	MissingImplementationException(Class<? extends Saveable> cls) {
        super("The class " + cls.getName() + " does not implement the static String SAVE_DIR which is used to allocate the directory in which objects of the type " + cls.getSimpleName() + " are saved in.");
    }
}
