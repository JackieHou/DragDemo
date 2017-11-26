package com.jackiehou.dragdemo;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.annimon.stream.Stream;
import com.annimon.stream.function.Consumer;
import com.jackiehou.dragdemo.manager.OrmHelper;
import com.jackiehou.dragdemo.views.CircleLayoutPlanB;
import com.jackiehou.dragdemo.views.DragHelper;
import com.jackiehou.dragdemo.views.DragLayout;


public class DragActivity extends BaseActivity {

    DragHelper dragHelper;

    LinearLayout mLeftLayout,mRightLayout;
    CircleLayoutPlanB mCircleLayout;


    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drag);


        mCircleLayout = (CircleLayoutPlanB) findViewById(R.id.cl);
        mLeftLayout = (LinearLayout) findViewById(R.id.left_ll);
        mRightLayout = (LinearLayout) findViewById(R.id.right_ll);

        ImageView imageView = new ImageView(this);
        imageView.setImageResource(R.mipmap.ic_launcher_round);
        mCircleLayout.setCenterView(imageView);

        DragLayout dragLayout = (DragLayout) findViewById(R.id.drag_rl);
        dragHelper = new DragHelper(dragLayout,mLeftLayout,mRightLayout,mCircleLayout);


        setupViews();

        //长安事件
        Stream.of(R.id.left_btn1,R.id.left_btn2,R.id.left_btn3,R.id.right_btn1,R.id.right_btn2,R.id.right_btn3,
                R.id.bottom_btn1,R.id.bottom_btn2,R.id.img1,R.id.img2,R.id.img3,R.id.img4,R.id.img5,R.id.img6)
                .map(id -> findViewById(id))
                .forEach(view -> view.setOnLongClickListener(v -> addToDrag(v)));

    }

    /**
     * 读取数据并更新view
     */
    @SuppressLint("NewApi")
    private void setupViews() {
        Stream.of(OrmHelper.getHelper().getAllDragItem())
                .flatMap(items -> Stream.of(items))
                .forEach(item ->{
                    int id = Utils.getResId(DragActivity.this,"id",item.getKey());
                    TextView view = (TextView) findViewById(id);
                    if(view instanceof CheckedTextView){
                        view.setBackgroundResource( Utils.getResId(DragActivity.this,"drawable",item.getIconName()));
                    }else {
                        view.setText(item.getTitle());
                    }
                    view.setTag(item);
                });
    }

    /**
     * 长按拖拽view
     * @param v
     * @return
     */
    @SuppressLint("NewApi")
    private boolean addToDrag(View v){
        ((Consumer<Boolean>)v).accept(true);

        dragHelper.addToDragLayout(DragActivity.this,(TextView) v);

        return false;
    }

}
