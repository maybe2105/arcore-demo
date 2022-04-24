package com.example.ardemo;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;

import com.google.ar.core.Anchor;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.assets.RenderableSource;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;

import java.util.concurrent.atomic.AtomicInteger;

public class MainActivity extends AppCompatActivity {
    private ArFragment arFragment;
    private String ASSET_3D ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AtomicInteger count = new AtomicInteger();
        arFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.arFragment);
        arFragment.setOnTapArPlaneListener(((hitResult, plane, motionEvent) -> {
            if(count.get() == 0){
                ASSET_3D = "models/gun.glb";
            }
            if(count.get() == 1){
                ASSET_3D = "models/Pistol.glb";
            }
            if(count.get() == 2){
                ASSET_3D = "models/Watergun.glb";
            }
            if(count.get() < 3) {
                placeModel(hitResult.createAnchor());
                count.getAndIncrement();
            }
        }));

    }

    private void placeModel(Anchor anchor) {
        ModelRenderable
                .builder()
                .setSource(
                        this,
                        RenderableSource
                                .builder()
                                .setSource(this, Uri.parse(ASSET_3D), RenderableSource.SourceType.GLB)
                                .setScale(0.75f)
                                .setRecenterMode(RenderableSource.RecenterMode.ROOT).build())
                .setRegistryId(ASSET_3D)
                .build()
                .thenAccept(modelRenderable -> addNodeToScene(modelRenderable, anchor))
                .exceptionally(throwable -> {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage(throwable.getMessage()).show();
                    return null;
                });
    }

    private void addNodeToScene(ModelRenderable renderable, Anchor anchor) {


        AnchorNode anchorNode = new AnchorNode(anchor);
        TransformableNode node = new TransformableNode(arFragment.getTransformationSystem());
        node.setRenderable(renderable);
        node.setParent(anchorNode);
        arFragment.getArSceneView().getScene().addChild(anchorNode);
        node.select();
    }
}