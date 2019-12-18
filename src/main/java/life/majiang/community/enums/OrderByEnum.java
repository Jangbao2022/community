package life.majiang.community.enums;

public enum OrderByEnum {

    ORDER_BY_ENUM(""),//自定义排序
    ORDER_BY_GMT_MODIFIED("gmt_modified desc"),//修改晚的排前面
    ORDER_BY_GMT_CREATE("gmt_create desc"),//创建晚的排前面
    ORDER_BY_STATUS("status");//未读排前面


    private String sort;

    OrderByEnum(String sort) {
        this.sort = sort;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public OrderByEnum addSort(String sort) {
        String newSort;
        if ("".equals(OrderByEnum.ORDER_BY_ENUM.getSort())) {
            newSort = sort;
        } else {
            newSort = this.sort + "," + sort;
        }
        ORDER_BY_ENUM.setSort(newSort);
        return ORDER_BY_ENUM;
    }


}
