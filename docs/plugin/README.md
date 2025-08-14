# 插件开发概述

插件由以下部分组成：
- Cube：继承 MosaicCube（或 AngelCube）
- 扩展包：继承 MosaicExtPackage，持有对 Cube 的引用
- 扩展点：在扩展包中通过 @MExtension 标注的方法
- 监听器：可选，继承 SelectiveMosaicEventListener

开发流程：
1. 编写 Cube 类并添加 @MCube 元数据
2. 编写扩展包与扩展点方法
3.（可选）编写监听器
4. 打包并由宿主安装到 Slot，执行调用