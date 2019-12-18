package life.majiang.community.cache;

import life.majiang.community.dto.TagDTO;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TagCache {


    public static List<TagDTO> get() {
        List<TagDTO> tagDTOS = new ArrayList<>();
        TagDTO school = new TagDTO();
        school.setCategoryName("小吃");
        school.setTags(Arrays.asList("紫菜", "奥尔良烤翅", "各类慕斯", "黑森林",
                "布朗尼", "提拉米苏", "牛排", "牛肉干", "奶昔", "奶茶", "双皮奶",
                "手抓饼", "巧克力", "咖喱鱼丸", "鱼腐", "寿司", "北极贝", "三文鱼",
                "天妇罗", "黑轮", "冰激凌", "炒面", "三文治", "鸡翅", "意大利面",
                "烧烤", "鳗鱼饭三吃", "拉面", "熟寿司", "荞麦面", "咚咚烧"));
        tagDTOS.add(school);

        TagDTO city = new TagDTO();
        city.setCategoryName("城市");
        city.setTags(Arrays.asList("北京", "上海", "广州", "深圳", "武汉", "成都"));
        tagDTOS.add(city);

        TagDTO country = new TagDTO();

        country.setCategoryName("国家");
        country.setTags(Arrays.asList("中国", "日本", "美国", "韩国", "法国", "英国"));
        tagDTOS.add(country);

        return tagDTOS;
    }

    public static String filterInvalid(String pubTags) {
        String[] tags = StringUtils.split(pubTags, ",");

        List<TagDTO> tagDTOS = get();

        List<String> tagList = tagDTOS.stream().flatMap(tag -> tag.getTags().stream()).collect(Collectors.toList());

        String inValid = Arrays.stream(tags).filter(t -> !tagList.contains(t)).collect(Collectors.joining(","));

        return inValid;
    }
}
