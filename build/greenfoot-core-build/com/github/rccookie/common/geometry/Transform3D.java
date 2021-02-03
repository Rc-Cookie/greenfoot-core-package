package com.github.rccookie.common.geometry;

import com.github.rccookie.common.data.Saveable;
import com.github.rccookie.common.data.json.JsonField;

public class Transform3D implements Cloneable, Saveable {

    public static final String SAVE_DIR = "saves\\geometry\\transforms";

    private static final long serialVersionUID = -7384556436740912937L;

    /**
     * Do not set to {@code null}!
     */
    @JsonField
    public Vector3D location;

    /**
     * Do not set to {@code null}!
     */
    @JsonField
    public Rotation rotation;

    /**
     * Do not set to {@code null}!
     */
    @JsonField
    public Vector3D scale;


    public Transform3D() {
        this(new Vector3D(), new Rotation(), new Vector3D(1, 1, 1));
    }
    public Transform3D(Transform3D copy) {
        this(
            copy.location.clone(),
            copy.rotation.clone(),
            copy.scale.clone()
        );
    }
    public Transform3D(Vector3D location) {
        this(location, new Rotation(), new Vector3D(1, 1 ,1));
    }
    public Transform3D(Vector3D location, Rotation rotation, Vector3D scale) {
        this.location = location;
        this.rotation = rotation;
        this.scale = scale;
    }



    @Override
    public Transform3D clone() {
        return new Transform3D(this);
    }





    @Override
    public String getSaveName() {
        if(saveName == null) return "transform" + hashCode();
        return saveName;
    }


    private String saveName = null;
    @Override
    public void setSaveName(String name) {
        saveName = name;
    }
}
