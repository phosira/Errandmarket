package Erannd.Market;

public class HomeItems {

    String home_item_emergency;
    String home_item_title;
    String home_item_picture;
    String home_item_village;
    String home_item_distance;
    String home_item_time;
    String home_item_price;
    String home_item_idx;


    public HomeItems(String home_item_emergency,
                     String home_item_title,
                     String home_item_picture,
                     String home_item_village,
                     String home_item_distance,
                     String home_item_time,
                     String home_item_price,
                     String home_item_idx) {

        this.home_item_emergency = home_item_emergency;
        this.home_item_title = home_item_title;
        this.home_item_picture = home_item_picture;
        this.home_item_village = home_item_village;
        this.home_item_distance = home_item_distance;
        this.home_item_time = home_item_time;
        this.home_item_price = home_item_price;
        this.home_item_idx = home_item_idx;
    }


        public String getHome_item_emergency() {
            return home_item_emergency;
        }

        public void setHome_item_emergency(String home_item_emergency) {
            this.home_item_emergency = home_item_emergency;
        }

        public String getHome_item_title() {
            return home_item_title;
        }

        public void setHome_item_title(String home_item_title) {
            this.home_item_title = home_item_title;
        }

        public String getHome_item_picture() {
            return home_item_picture;
        }

        public void setHome_item_picture(String home_item_picture) {
            this.home_item_picture = home_item_picture;
        }

        public String getHome_item_village() {
            return home_item_village;
        }

        public void setHome_item_village(String home_item_village) {
            this.home_item_village = home_item_village;
        }

        public String getHome_item_distance() {
            return home_item_distance;
        }

        public void setHome_item_distance(String home_item_distance) {
            this.home_item_distance = home_item_distance;
        }

        public String getHome_item_time() {
            return home_item_time;
        }

        public void setHome_item_time(String home_item_time) {
            this.home_item_time = home_item_time;
        }

        public String getHome_item_price() {
            return home_item_price;
        }

        public void setHome_item_price(String home_item_price) {
            this.home_item_price = home_item_price;
        }

    public String getHome_item_idx() {
        return home_item_idx;
    }

    public void setHome_item_idx(String home_item_idx) {
        this.home_item_idx = home_item_idx;
    }

}
