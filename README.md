# EBookReader

一个简单的 Android 电子书阅读器应用，使用 Jetpack Compose 构建。

## 功能特点

- 支持导入和阅读 TXT 格式的电子书
- 使用 Material Design 3 的现代界面设计
- 支持浅色/深色主题手动切换
- 支持跟随系统主题设置
- 支持动态主题颜色 (Android 12+)
- 书籍自动保存和加载
- 简洁直观的阅读界面
- 支持书籍删除功能
- 支持书籍列表管理
- 支持设置页面

## 主题设置

应用提供了灵活的主题设置选项：

- **手动切换**：通过顶部栏的主题图标可以手动切换浅色/深色主题
- **跟随系统**：可以选择跟随系统设置，自动适应系统的浅色/深色主题
- **主题持久化**：应用会记住您的主题偏好，下次启动时自动应用

## 技术栈

- Kotlin
- Jetpack Compose
- Material 3
- Navigation Compose
- Coroutines
- DataStore Preferences
- Android Architecture Components (ViewModel)

## 项目结构

- `ui/theme`: Material Design 主题配置
- `repository`: 书籍数据管理
- `model`: 数据模型
- `navigation`: 应用导航逻辑
- `components`: UI 组件
- `viewmodel`: 视图模型
- `data`: 数据存储和偏好设置

## 开发工具

- Android Studio
- Cursor AI

## 许可证

本项目采用 MIT 许可证。
