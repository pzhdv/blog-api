package cn.pzhdv.blog.utils;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.IFill;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.fill.Column;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author PanZonghui
 * @Version: 1.0
 * @Description mybatis-plus代码生成器
 * @since 2025-06-25 13:10:16
 */
public class MPCodeGenerator {

    private static String url = "jdbc:mysql://localhost:3306/pzh_blog?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=UTC";
    private static String username = "root";
    private static String password = "root";
    private static String authorName = "PanZonghui";
    private static String parentPackage = "cn.pzhdv.blog";

    private static String[] tablePrefix = {};// 表前缀 ,
    private static String[] tableNames = {"system_user"};// 要生成的表名 {} 表示生成所有表

    private static String versionColumnName = "version"; // 版本号,用于乐观锁  数据库中默认值设置为: 1  字段上面添加 @JsonIgnore 不返回给前端
    private static String logicDeleteColumnName = "deleted"; // 逻辑删除字段名(0:未删除 1:已删除) 数据库中默认值设置为: 0

    // 自定义需要填充的字段 数据库中的字段
    private static List<IFill> columnList = new ArrayList<>();

    static {
        columnList.add(new Column(versionColumnName, FieldFill.INSERT));
        columnList.add(new Column(logicDeleteColumnName, FieldFill.INSERT));
        columnList.add(new Column("create_time", FieldFill.INSERT)); // 数据库创建时间字段名
        columnList.add(new Column("update_time", FieldFill.INSERT_UPDATE)); // 数据库更新时间字段名
    }

    /**
     * 使用方式 【特殊字段】
     *
     * @ApiModelProperty(value = "创建时间")
     * @TableField(value = "create_time",fill = FieldFill.INSERT)
     * @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
     * private Date createTime;
     * @ApiModelProperty(value = "更新时间")
     * @TableField(value = "update_time",fill = FieldFill.INSERT_UPDATE)
     * @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
     * private Date updateTime;
     * @JsonIgnore //使用@JsonIgnore注解，忽略此属性，前端不会拿到该属性
     * @ApiModelProperty("版本号")
     * @TableField(value = "version", fill = FieldFill.INSERT)
     * @Version private Integer version;
     */

    public static void main(String[] args) {

        FastAutoGenerator.create(url, username, password)
                //全局配置
                .globalConfig(builder -> {
                    builder
//                            .fileOverride() //开启覆盖之前生成的文件 ⭐⭐⭐⭐⭐⭐
                            .disableOpenDir()  // 禁止打开输出目录
                            .author(authorName) //生成的作者名字
                            .enableSwagger() //开启swagger
                            .dateType(DateType.ONLY_DATE)   //定义生成的实体类中日期类型 DateType.ONLY_DATE 默认值: DateType.TIME_PACK
                            .commentDate("yyyy-MM-dd HH:mm:ss")//注释日期
                            .outputDir(System.getProperty("user.dir") + "/src/main/java");//指定输出目录
                })
                //包配置
                .packageConfig(builder -> {
                    builder
                            .parent(parentPackage)//父包名
                            .entity("entity")//实体类包名
                            .controller("controller")//控制层包名
                            .service("service")//service层包名
                            .serviceImpl("service.impl")//service实现类包名
                            .mapper("mapper")//mapper层包名
                            //.moduleName("educms") // 设置父包模块名 默认值:无
                            //设置mapperXml生成路径
                            .pathInfo(Collections.singletonMap(OutputFile.mapperXml, System.getProperty("user.dir") + "/src/main/resources/mapper"));
                })
                //策略配置
                .strategyConfig(builder -> {
                    builder
                            .addInclude(tableNames)//设置要生成的表名
                            .addTablePrefix(tablePrefix)//表名前缀，配置后生成的代码不会有此前缀，建议表名的驼峰格式命名，方便寻找对应的文件资料
                            .entityBuilder() //1：entity策略配置
                            .formatFileName("%s")//格式化实体名称，%s取消首字母I,
                            .enableLombok()//实体类使用lombok,需要自己引入依赖
                            .enableChainModel()
                            .naming(NamingStrategy.underline_to_camel)//数据表映射实体命名策略：默认下划线转驼峰underline_to_camel
                            .columnNaming(NamingStrategy.underline_to_camel)//表字段映射实体属性命名规则：默认null，不指定按照naming执行
                            .idType(IdType.AUTO)// 主键策略 IdType.ASSIGN_ID:雪花算法自动生成的id
                            .enableTableFieldAnnotation() // 属性加上注解说明
                            .versionColumnName(versionColumnName)//乐观锁字段名
                            .logicDeleteColumnName(logicDeleteColumnName)//逻辑删除字段名
                            .addTableFills(columnList)// 自动填充配置
                            .controllerBuilder()//2：controller策略配置
                            .formatFileName("%sController")//控制类名称后缀
                            .enableRestStyle() //开启restful风格
                            .serviceBuilder()//3：service策略配置
                            .formatServiceFileName("%sService")//服务层接口名后缀
                            .formatServiceImplFileName("%sServiceImpl")//服务层实现类名后缀
                            .mapperBuilder()//4：mapper策略配置
                            .enableMapperAnnotation()//开启@mapper注解
                            .enableBaseResultMap()//启用xml文件中的BaseResultMap 生成
                            .enableBaseColumnList()//启用xml文件中的BaseColumnList
                            .formatMapperFileName("%sMapper")//Dao层接口名后缀
                            .formatXmlFileName("%sMapper");//格式化xml文件名称后缀

                })
                .execute();
    }
}
