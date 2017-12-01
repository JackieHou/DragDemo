package com.jackiehou.dragdemo;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.annimon.stream.Stream;
import com.annimon.stream.function.Consumer;
import com.jackiehou.dragdemo.db.OrmHelper;
import com.jackiehou.dragdemo.views.CircleLayout;
import com.jackiehou.dragdemo.views.DragHelper;
import com.jackiehou.dragdemo.views.DragLayout;


public class DragActivity extends BaseActivity {

    DragHelper dragHelper;

    LinearLayout mLeftLayout,mRightLayout;
    CircleLayout mCircleLayout;

    DragLayout dragLayout;

    ScrollView scrollView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drag);


        mCircleLayout = (CircleLayout) findViewById(R.id.cl);
        mLeftLayout = (LinearLayout) findViewById(R.id.left_ll);
        mRightLayout = (LinearLayout) findViewById(R.id.right_ll);
        LinearLayout bottomLayout =(LinearLayout) findViewById(R.id.bottom_ll);

        /*ImageView imageView = new ImageView(this);
        imageView.setImageResource(R.mipmap.steeringwheel);
        mCircleLayout.setCenterView(imageView);*/

        scrollView = (ScrollView) findViewById(R.id.sv);
        mCircleLayout.setScrollView(scrollView);

        dragLayout = (DragLayout) findViewById(R.id.drag_rl);
        dragHelper = new DragHelper(dragLayout,mLeftLayout,mRightLayout,mCircleLayout,bottomLayout);
        dragHelper.setOnDragEndCallback(() -> setupViews());

        //长安事件
        Stream.of(R.id.left_btn1,R.id.left_btn2,R.id.left_btn3,R.id.right_btn1,R.id.right_btn2,R.id.right_btn3,
                R.id.bottom_btn1,R.id.bottom_btn2,R.id.img1,R.id.img2,R.id.img3,R.id.img4,R.id.img5,R.id.img6)
                .map(id -> findViewById(id))
                .forEach(view -> view.setOnLongClickListener(v -> addToDrag(v)));

        Stream.of(R.id.left_btn1,R.id.left_btn2,R.id.left_btn3,R.id.right_btn1,R.id.right_btn2,R.id.right_btn3,
                R.id.bottom_btn1,R.id.bottom_btn2,R.id.img1,R.id.img2,R.id.img3,R.id.img4,R.id.img5,R.id.img6)
                .map(id -> findViewById(id))
                .forEach(view -> view.setOnClickListener(v -> Toast.makeText(DragActivity.this,"11111",Toast.LENGTH_SHORT).show()));

    }

    @Override
    protected void onStart() {
        super.onStart();
        setupViews();
    }

    /**
     * 读取数据并更新view
     */
    private void setupViews() {
        OrmHelper.getHelper().cleanCache();
        Stream.of(OrmHelper.getHelper().getAllDragItem())
                .flatMap(items -> Stream.of(items))
                .forEach(item ->{
                    int id = Utils.getResId(DragActivity.this,"id",item.getKey());
                    View view = findViewById(id);
                    if(view instanceof TextView){
                        ((TextView)view).setText(item.getTitle());
                    }else {
                        view.setBackgroundResource( Utils.getResId(DragActivity.this,"drawable",item.getIconName()));
                    }
                    view.setTag(item);
                });
    }

    /**
     * 长按拖拽view
     * @param v
     * @return
     */
    private boolean addToDrag(View v){
        ((Consumer<Boolean>)v).accept(true);
        //boolean actionUp = ((Supplier<Boolean>)v).get();
        //Log.w("DragActivity","addToDrag actionUp="+actionUp);
        ///if(!actionUp){
        //if(!mCircleLayout.getDragging()){
        dragLayout.requestDisallowInterceptTouchEvent(false);
            dragHelper.addToDragLayout(DragActivity.this, v);
        //}

        //}


        return true;
    }

}
