package cn.chien.poi.annotation

/**
 * 标注excel导入/导出时实体类的标识字段，用于合并数据
 *
 * @author qiandq3
 * @date 2022/12/9
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class ExcelClass(val key: String = "")
