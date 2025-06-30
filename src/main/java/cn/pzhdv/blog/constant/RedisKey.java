package cn.pzhdv.blog.constant;

/**
 * @author PanZonghui
 * @Version: 1.0
 * @Description redis中的key
 * @since 2025-04-24 13:10:16
 */
public class RedisKey {

    /**
     * 文章分类表缓存的key
     */
    public static final String ARTICLE_CATEGORY_TOTAL_KEY = "article:category:total";
    public static final String ARTICLE_CATEGORY_LIST_KEY = "article:category:list";
    public static final String ARTICLE_CATEGORY_TREE_LIST_KEY = "article:category:tree:list";

    /**
     * 文章标签表缓存的key
     */
    public static final String ARTICLE_TAG_CATCH_KEY = "article_tag_catch_key-";

    /**
     * 博客作者表缓存的key
     */
    public static final String BLOG_AUTHOR_CATCH_KEY = "blog_author_catch_key-";

    /**
     * 博客使命表缓存的key
     */
    public static final String BLOG_MISSION_CATCH_KEY = "blog_mission_catch_key-";

    /**
     * 经历表缓存的key
     */
    public static String JOB_EXPERIENCE_CATCH_KEY = "job_experience_catch_key-";

    /**
     * 文字相关的key
     */
    public static final String ARTICLE_TOTAL_KEY = "article:total";
    public static final String ARTICLE_PUBLISH_DATE_LIST_KEY = "article:publishDateList";
    public static final String ARTICLE_HOME_PAGE_LIST_KEY = "article:homePageList";
    public static final String ARTICLE_CATEGORY_PAGE_LIST_KEY = "article:categoryPageList";
    public static final String ARTICLE_CONDITION_PAGE_LIST_KEY = "article:conditionPageList";
    public static final String ARTICLE_DETAIL_KEY = "article:detail:";
}
