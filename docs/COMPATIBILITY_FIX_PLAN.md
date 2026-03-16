# Play Plugin 2025.3.3 兼容性问题修复计划

## 问题描述

- **症状**: 在 IntelliJ IDEA 2025.3.3 安装 Play 插件后，插件本身功能失效，且导致所有 Java 类中的引用无法跳转
- **影响范围**: 全局 - 不仅是 Play 相关文件，所有 Java 文件的导航功能都被破坏
- **目标版本**: 2024.1 - 2025.3.3

---

## 根因分析

### 1. API 版本严重不匹配 (最可能根因)

| 配置项 | 当前值 | 风险 |
|-------|-------|------|
| 编译目标 IDE | 2024.1 | 使用 2024.1 API 编译 |
| 声明兼容范围 | 241 - 253.* (2025.3.3) | 运行时使用 2025.3.3 |
| 问题 | **编译时 vs 运行时 API 差异** | 2024.1 → 2025.3.3 有大量 API 变更 |

**问题本质**: 插件用 2024.1 的 SDK 编译，但声明支持 2025.3.3。2025.3.x 相比 2024.1 有重大平台变更:
- PSI API 调整 (CachedValue, StubIndex, PsiReference)
- LanguageSubstitutor 行为变化
- Groovy 插件 API 变更 (K2 模式)
- Workspace Model 成为默认

### 2. 高风险扩展点注册

以下扩展点注册过于宽泛，可能在 2025.3 中导致全局问题:

| 行号 | 扩展点 | 目标 | 风险级别 |
|-----|-------|------|---------|
| 64 | `elementManipulator` for `PsiPlainText` | **所有纯文本文件** | 🔴 高 |
| 45 | `psi.referenceContributor` | **全局 (无语言过滤)** | 🔴 高 |
| 32-33 | `lang.substitutor` for HTML/JSP | HTML/JSP 文件 | 🟡 中 |
| 41 | `codeInsight.lineMarkerProvider` for JAVA | Java 文件 | 🟡 中 |
| 70 | `useScopeEnlarger` | 全局作用域 | 🟡 中 |

### 3. 关键可疑代码

#### PlainTextManipulator (plugin.xml:64)
```java
// 注册为所有 PsiPlainText 服务
<lang.elementManipulator forClass="com.intellij.psi.PsiPlainText" ...>
```
这是**全局注册**，任何纯文本文件都会使用这个 manipulator。在 2025.x 中，如果 PSI 层级有变化，这个 broad registration 可能干扰正常的引用解析。

#### PlayReferenceContributor (plugin.xml:45)
```xml
<psi.referenceContributor implementation="com.intellij.play.references.PlayReferenceContributor"/>
```
**没有指定 language 属性**，作为全局 reference contributor 注册。如果 `PlayReferenceContributor` 的实现有缺陷或与 2025.x 不兼容，可能影响所有文件的引用解析。

---

## 验证计划

### Phase 1: 确认根因 (必须按顺序)

| 步骤 | 操作 | 验证方法 | 预期结果 |
|-----|------|---------|---------|
| 1.1 | 回滚之前的修改 (PlayReferenceContributor.java, PlayRoutesPsiReferenceProvider.java) | git diff 检查 | 恢复到修改前状态 |
| 1.2 | 仅用原始插件 (无我的修改) 打包 | `./gradlew buildPlugin` | 生成原始版 ZIP |
| 1.3 | 在 2025.3.3 安装原始插件 | 手动安装 | **确认原始插件是否也破坏 Java 导航** |
| 1.4 | 如果原始插件也破坏 → 确认根因是 API 不匹配 | - | - |
| 1.5 | 如果原始插件正常 → 根因是最近的修改 | - | - |

**关键验证点**: 必须先确认是"历史遗留问题"还是"我的修改导致的新问题"

### Phase 2: 如果确认是 API 版本不匹配

| 步骤 | 操作 | 验证方法 |
|-----|------|---------|
| 2.1 | 升级编译目标到 2025.3.x | 修改 build.gradle.kts: intellijIdeaUltimate("2025.3") |
| 2.2 | 重新编译打包 | `./gradlew buildPlugin` |
| 2.3 | 在 2025.3.3 验证 | 安装并测试 Java 导航 |
| 2.4 | 检查是否需要代码修改 | 运行测试，观察错误 |

### Phase 3: 如果确认是我的修改导致

| 步骤 | 操作 | 验证方法 |
|-----|------|---------|
| 3.1 | 逐个回滚我的修改 | git checkout 具体文件 |
| 3.2 | 每次回滚后打包验证 | ./gradlew buildPlugin → 安装测试 |
| 3.3 | 定位导致问题的具体修改 | 二分法定位 |

---

## 修复方案

### 方案 A: 升级编译目标 (推荐)

**原理**: 用 2025.3.x SDK 编译插件，让插件使用最新的 API

```kotlin
// build.gradle.kts 修改
intellijPlatform {
    dependencies {
        intellijPlatform {
            intellijIdeaUltimate("2025.3")  // ← 升级到 2025.3
            // ...
        }
    }
}
```

**优点**: 
- 直接使用 2025.3 API，无兼容层
- 未来版本继续兼容

**缺点**:
- 需要 2025.3  SDK (可能需要下载)
- 可能需要代码调整 (如果 API 有 breaking changes)

### 方案 B: 缩小兼容范围

**原理**: 如果无法支持 2025.3，则明确声明不支持

```kotlin
// build.gradle.kts 修改
intellijPlatform {
    pluginConfiguration {
        ideaVersion {
            sinceBuild = "241"
            untilBuild = "243.*"  // ← 最多支持到 2024.3
        }
    }
}
```

**优点**: 
- 避免用户误用在不支持的版本
- 无需代码修改

**缺点**:
- 不满足用户需求 (用户要在 2025.3 用)

### 方案 C: 修复扩展点注册 (如果前两个方案都不可行)

**原理**: 缩小 `PlainTextManipulator` 和 `PlayReferenceContributor` 的作用范围

1. **修改 PlainTextManipulator**:
   - 方案: 添加文件类型检查，只处理 Play 相关文件
   - 或: 在 plugin.xml 中移除这个全局注册

2. **修改 PlayReferenceContributor**:
   - 方案: 添加语言过滤，只处理 Play 文件
   - 或: 修复 `getReferencesByElement` 中的潜在 NPE/异常

---

## 执行决策点

| 决策点 | 条件 | 选择 |
|-------|------|------|
| D1 | 原始插件在 2025.3.3 也破坏 Java 导航? | 是 → 方案 A 或 B; 否 → 回滚我的修改 |
| D2 | 升级到 2025.3 SDK 可行? | 是 → 方案 A; 否 → 方案 B 或 C |
| D3 | 是否有代码变更需要? | 是 → 按需修改; 否 → 仅配置变更 |

---

## 待执行命令 (仅供记录)

```bash
# 1. 查看修改历史
git diff HEAD -- src/main/java/com/intellij/play/references/

# 2. 回滚我的修改 (如果需要)
git checkout HEAD -- src/main/java/com/intellij/play/references/PlayReferenceContributor.java
git checkout HEAD -- src/main/java/com/intellij/play/references/PlayRoutesPsiReferenceProvider.java

# 3. 打包
./gradlew buildPlugin

# 4. 验证插件
# 手动安装到 2025.3.3 并测试 Java 导航

# 5. 升级 SDK (如需要)
# 修改 build.gradle.kts 中的 intellijIdeaUltimate 版本
```

---

## 根因确认 (Librarian 调研结果)

根据 IntelliJ Platform 官方变更日志，以下是 2024.2-2025.3 间的关键 API 变更:

| 版本 | 变更领域 | 具体 API / 详情 |
|-----|---------|----------------|
| **2024.2** | **运行时** | **Java 21** 成为 IDE 和插件运行的最低要求 |
| **2024.2** | **分析 API** | `KtModule` 重命名为 `KaModule` |
| **2024.3** | **PSI Stubs** | `StubElement.getChildrenStubs()` 返回类型改为 `List<StubElement<?>>` |
| **2025.1** | **线程模型** | **`Write-Intent lock` 从 `invokeLater` 和 `Dispatchers.Main` 中移除** |
| **2025.1** | **Kotlin** | **K2 Mode 默认启用**; 需要 Analysis API 兼容性 |
| **2025.3** | **分发版** | 统一 IDE; 模块提取到独立 classloader |

### 最可能根因 (综合分析)

基于上述变更，**最可能的根因是**:

1. **LanguageSubstitutor 兼容性断裂 (最直接)**:
   - `PlayLanguageSubstitutor` 注册为 HTML/JSP 的 substitutor
   - 在 2025.x 中，如果 substitutor 返回无效语言或行为异常，会导致 `FileViewProvider` 创建失败
   - 结果: Java 文件无法被正确识别为 Java PSI → **全局导航失效**

2. **K2 Mode 干扰 (次可能)**:
   - 2025.1+ 启用 K2 Mode 后，混合语言项目的 PSI 解析机制有变化
   - 如果 Play 插件的 reference contributor 不兼容 K2，会导致解析引擎静默失败

3. **线程模型变更 (辅助)**:
   - 2025.1 移除了 Write-Intent lock
   - 如果插件在后台线程执行引用解析，可能因锁问题导致静默异常

---

## 结论

**核心根因**: `PlayLanguageSubstitutor` 在 2025.3 环境中可能返回了无效语言或触发了 FileViewProvider 创建失败，导致 Java 文件无法被正确解析，进而破坏了全局引用跳转。

**建议立即执行**: 
1. 先用 git 回滚我的修改，用原始代码打包验证
2. 如果原始代码也破坏 → 定位到 LanguageSubstitutor 问题 → 修复或移除该扩展点
3. 如果原始代码正常 → 逐步分析我修改中的问题

---

*文档创建时间: 2026-03-15*
*分析模式: 已完成调研，计划已文档化，等待用户确认执行*
