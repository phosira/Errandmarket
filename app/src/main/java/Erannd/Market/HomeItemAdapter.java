package Erannd.Market;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import javax.mail.Quota;
import java.util.ArrayList;

public class HomeItemAdapter extends RecyclerView.Adapter<HomeItemAdapter.ViewHolder>{
    Context context;
    ArrayList<HomeItems> items = new ArrayList<HomeItems>();


    OnItemClickListener listener; //참고로 OnItemClickListener는 기존에 있는것과 동일한 이름인데 그냥 같은 이름으로 내가 정의를 했다. (리스트뷰에서는 이게 자동구현되있어서 OnItemClickListener를 구현안하고 호출해서 클릭시 이벤트를 처리할 수 있음)
    public  static interface  OnItemClickListener{
        public void onItemClick(RecyclerView.ViewHolder holder, View view, int position);
    }



    public  HomeItemAdapter(Context context){
        this.context =  context;
    }


    @Override //어댑터에서 관리하는 아이템의 개수를 반환
    public int getItemCount() {
        return items.size();
    }


    @NonNull
    @Override //뷰홀더가 만들어지는 시점에 호출되는 메소드(각각의 아이템을 위한 뷰홀더 객체가 처음만들어지는시점)
    //만약에 각각의 아이템을 위한 뷰홀더가 재사용될 수 있는 상태라면 호출되지않음 (그래서 편리함, 이건내생각인데 리스트뷰같은경우는 convertView로 컨트롤해줘야하는데 이건 자동으로해줌)
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.activity_home_items,  viewGroup, false);//viewGroup는 각각의 아이템을 위해서 정의한 xml레이아웃의 최상위 레이아우싱다.

        return new ViewHolder(itemView); //각각의 아이템을 위한 뷰를 담고있는 뷰홀더객체를 반환한다.(각 아이템을 위한 XML 레이아웃을 이용해 뷰 객체를 만든 후 뷰홀더에 담아 반환
    }



    //각각의 아이템을 위한 뷰의 xml레이아웃과 서로 뭉쳐지는(결합되는) 경우 자동으로 호출( 즉 뷰홀더가 각각의 아이템을 위한 뷰를 담아주기위한 용도인데 뷰와 아이템이 합질때 호출)
    // Replace the contents of a view //적절한 데이터를 가져와 뷰 소유자의 레이아웃을 채우기 위해 사용(뷰홀더에 각 아이템의 데이터를 설정함)
    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {


        HomeItems item = items.get(position); //리사이클러뷰에서 몇번쨰게 지금 보여야되는시점이다 알려주기위해
        viewHolder.setItem(item); //그거를 홀더에넣어서 뷰홀더가 데이터를 알 수 있게되서 뷰홀더에 들어가있는 뷰에다가 데이터 설정할 수 있음
        //클릭리스너
        viewHolder.setOnItemClickListener(listener);

    }

    //아이템을 한개 추가해주고싶을때
    public  void addItem(HomeItems item){
        items.add(item);
    }

    public void clear(){items.clear(); notifyDataSetChanged();}

    //한꺼번에 추가해주고싶을때
    public void addItems(ArrayList<HomeItems> items){
        this.items = items;
    }


    public  HomeItems getItem(int position){
        return  items.get(position);
    }

    //클릭리스너관련
    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }

    //뷰홀더
    //뷰홀더 객체는 뷰를 담아두는 역할을 하면서 동시에 뷰에 표시될 데이터를 설정하는 역할을 맡을 수 있습니다.
    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView home_item_emegency;
        ImageView home_item_picture;
        TextView home_item_title;
        TextView home_item_village;
        TextView home_item_distance;
        TextView home_item_time;
        EditText home_item_price;
        TextView home_item_idx;
        TextView home_item_won;

        OnItemClickListener listenr; //클릭이벤트처리관련 변수

        public ViewHolder(@NonNull final View itemView) { //뷰홀더는 각각의 아이템을 위한 뷰를 담고있다.
            super(itemView);

            home_item_emegency = itemView.findViewById(R.id.home_item_emegency);
            home_item_picture = itemView.findViewById(R.id.home_item_picture);
            home_item_title = itemView.findViewById(R.id.home_item_title);
            home_item_village = itemView.findViewById(R.id.home_item_village);
            home_item_distance = itemView.findViewById(R.id.home_item_distance);
            home_item_time = itemView.findViewById(R.id.home_item_time);
            home_item_price = itemView.findViewById(R.id.home_item_price);
            home_item_idx = itemView.findViewById(R.id.home_item_idx);
            home_item_won = itemView.findViewById(R.id.home_item_won);
            home_item_price.setFocusable(false);
            home_item_price.setClickable(false);
            //아이템 클릭이벤트처리
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if(listenr != null ){
                        listenr.onItemClick(ViewHolder.this, itemView, position);
                    }
                }
            });
            ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        }

        //setItem 메소드는 SingerItem 객체를 전달받아 뷰홀더 안에 있는 뷰에 데이터를 설정하는 역할을 합니다.
        public void setItem(HomeItems item) {



                if(item.getHome_item_picture().equals("null")){
                    home_item_picture.setVisibility(View.GONE);
                }
            //if(!checkImageResource(home_item_picture.getContext(), home_item_picture,R.drawable.ic_baseline_add_photo_alternate_24)){
            //    home_item_picture.setVisibility(View.GONE);
            //}
            home_item_title.setText(item.getHome_item_title());
            home_item_village.setText(item.getHome_item_village());
            home_item_distance.setText(item.getHome_item_distance());
            home_item_time.setText(item.getHome_item_time());
            Log.v("가격",item.getHome_item_price());
            home_item_price.addTextChangedListener(new NumberTextWatcher(home_item_price));
            if(item.getHome_item_price().equals("0")){
                home_item_price.setText("가격흥정");
                home_item_won.setVisibility(View.GONE);
            }else{
                home_item_price.setText(item.getHome_item_price());
            }
            home_item_emegency.setText(item.getHome_item_emergency());
            if(item.getHome_item_emergency().equals("긴급 심부름")){
                home_item_emegency.setTextColor(0xAAef484a);
            }else{
                home_item_emegency.setTextColor(0xAAef484a);
            }
            home_item_idx.setText(item.getHome_item_idx());
            home_item_idx.setVisibility(View.GONE);
            Log.d("아이템 넘버",home_item_idx.getText().toString()+"");
        }

        //클릭이벤트처리
        public void setOnItemClickListener(OnItemClickListener listenr){
            this.listenr = listenr;
        }


    }

    public static boolean checkImageResource(Context ctx, ImageView imageView,
                                             int imageResource) {
        boolean result = false;

        if (ctx != null && imageView != null && imageView.getDrawable() != null) {
            Drawable.ConstantState constantState;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                constantState = ctx.getResources()
                        .getDrawable(imageResource, ctx.getTheme())
                        .getConstantState();
            } else {
                constantState = ctx.getResources().getDrawable(imageResource)
                        .getConstantState();
            }

            if (imageView.getDrawable().getConstantState() == constantState) {
                result = true;
            }
        }

        return result;
    }
}
