# pom.xml — line-by-line explanation

[Guide index](README.md) · [Exact source snapshot](https://github.com/ceias/minions/blob/dba2d321e276383939b0bf661659738741bdd343/MiniOS-Java/javafx/pom.xml)

Path: `MiniOS-Java/javafx/pom.xml`. Source revision: `dba2d32`.

This Maven project builds the JavaFX version with Java21, downloads JavaFX and test libraries, and provides javafx:run. sourceDirectory points at the parent MiniOS-Java directory so the original engines are compiled directly instead of copied. Compiler includes keep recursive target/test output from being treated as main source. A Maven-produced JAR is not automatically a standalone JavaFX installation.

Each row preserves one physical source line. Dense lines can contain several statements. Blank lines and closing delimiters are included so numbering matches your editor.

## Project identity

| Line | Original code | Explanation |
|---:|---|---|
| [L1](https://github.com/ceias/minions/blob/dba2d321e276383939b0bf661659738741bdd343/MiniOS-Java/javafx/pom.xml#L1) | <code>&lt;project xmlns=&quot;http://maven.apache.org/POM/4.0.0&quot; xmlns:xsi=&quot;http://www.w3.org/2001/XMLSchema-instance&quot; xsi:schemaLocation=&quot;http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd&quot;&gt;</code> | Declare a Maven POM using its XML namespace and schema. |
| [L2](https://github.com/ceias/minions/blob/dba2d321e276383939b0bf661659738741bdd343/MiniOS-Java/javafx/pom.xml#L2) | <code>  &lt;modelVersion&gt;4.0.0&lt;/modelVersion&gt;</code> | Use Maven&#x27;s4.0.0 POM model format; this is not the installed Maven executable version. |
| [L3](https://github.com/ceias/minions/blob/dba2d321e276383939b0bf661659738741bdd343/MiniOS-Java/javafx/pom.xml#L3) | <code>  &lt;groupId&gt;com.minios&lt;/groupId&gt;&lt;artifactId&gt;mini-os-fx&lt;/artifactId&gt;&lt;version&gt;1.0.0&lt;/version&gt;</code> | Set project coordinates com.minios:mini-os-fx:1.0.0. |
## Compiler settings

| Line | Original code | Explanation |
|---:|---|---|
| [L4](https://github.com/ceias/minions/blob/dba2d321e276383939b0bf661659738741bdd343/MiniOS-Java/javafx/pom.xml#L4) | <code>  &lt;properties&gt;&lt;maven.compiler.source&gt;21&lt;/maven.compiler.source&gt;&lt;maven.compiler.target&gt;21&lt;/maven.compiler.target&gt;&lt;project.build.sourceEncoding&gt;UTF-8&lt;/project.build.sourceEncoding&gt;&lt;/properties&gt;</code> | Compile Java21 syntax/bytecode and interpret source text as UTF-8. |
## Dependencies

| Line | Original code | Explanation |
|---:|---|---|
| [L5](https://github.com/ceias/minions/blob/dba2d321e276383939b0bf661659738741bdd343/MiniOS-Java/javafx/pom.xml#L5) | <code>  &lt;dependencies&gt;</code> | Begin dependency declarations. |
| [L6](https://github.com/ceias/minions/blob/dba2d321e276383939b0bf661659738741bdd343/MiniOS-Java/javafx/pom.xml#L6) | <code>    &lt;dependency&gt;&lt;groupId&gt;org.openjfx&lt;/groupId&gt;&lt;artifactId&gt;javafx-controls&lt;/artifactId&gt;&lt;version&gt;21.0.2&lt;/version&gt;&lt;/dependency&gt;</code> | Add JavaFX Controls21.0.2, which brings the graphics/base dependencies needed by the GUI. |
| [L7](https://github.com/ceias/minions/blob/dba2d321e276383939b0bf661659738741bdd343/MiniOS-Java/javafx/pom.xml#L7) | <code>    &lt;dependency&gt;&lt;groupId&gt;org.junit.jupiter&lt;/groupId&gt;&lt;artifactId&gt;junit-jupiter&lt;/artifactId&gt;&lt;version&gt;5.11.4&lt;/version&gt;&lt;scope&gt;test&lt;/scope&gt;&lt;/dependency&gt;</code> | Add JUnit Jupiter5.11.4 only for tests, not normal app runtime. |
| [L8](https://github.com/ceias/minions/blob/dba2d321e276383939b0bf661659738741bdd343/MiniOS-Java/javafx/pom.xml#L8) | <code>    &lt;dependency&gt;&lt;groupId&gt;org.testfx&lt;/groupId&gt;&lt;artifactId&gt;openjfx-monocle&lt;/artifactId&gt;&lt;version&gt;21.0.2&lt;/version&gt;&lt;scope&gt;test&lt;/scope&gt;&lt;/dependency&gt;</code> | Add Monocle21.0.2 only for headless JavaFX tests. |
| [L9](https://github.com/ceias/minions/blob/dba2d321e276383939b0bf661659738741bdd343/MiniOS-Java/javafx/pom.xml#L9) | <code>  &lt;/dependencies&gt;</code> | Part of the generated Apache Maven bootstrap/control syntax; it supports locating, downloading or invoking Maven rather than implementing OS algorithms. |
## Build plugins

| Line | Original code | Explanation |
|---:|---|---|
| [L10](https://github.com/ceias/minions/blob/dba2d321e276383939b0bf661659738741bdd343/MiniOS-Java/javafx/pom.xml#L10) | <code>  &lt;build&gt;</code> | Begin build configuration. |
| [L11](https://github.com/ceias/minions/blob/dba2d321e276383939b0bf661659738741bdd343/MiniOS-Java/javafx/pom.xml#L11) | <code>    &lt;!-- Share the original engines; keep JavaFX out of the Swing wildcard build. --&gt;</code> | Explain why parent source files are shared. |
| [L12](https://github.com/ceias/minions/blob/dba2d321e276383939b0bf661659738741bdd343/MiniOS-Java/javafx/pom.xml#L12) | <code>    &lt;sourceDirectory&gt;${project.basedir}/..&lt;/sourceDirectory&gt;</code> | Use the parent folder as Java source root, giving direct access to all engine classes. |
| [L13](https://github.com/ceias/minions/blob/dba2d321e276383939b0bf661659738741bdd343/MiniOS-Java/javafx/pom.xml#L13) | <code>    &lt;plugins&gt;</code> | Begin plugin declarations. |
| [L14](https://github.com/ceias/minions/blob/dba2d321e276383939b0bf661659738741bdd343/MiniOS-Java/javafx/pom.xml#L14) | <code>      &lt;plugin&gt;&lt;groupId&gt;org.apache.maven.plugins&lt;/groupId&gt;&lt;artifactId&gt;maven-compiler-plugin&lt;/artifactId&gt;&lt;version&gt;3.13.0&lt;/version&gt;&lt;configuration&gt;&lt;includes&gt;&lt;include&gt;*.java&lt;/include&gt;&lt;include&gt;javafx/src/main/java/*.java&lt;/include&gt;&lt;/includes&gt;&lt;/configuration&gt;&lt;/plugin&gt;</code> | Pin compiler plugin3.13.0 and include only parent-root Java plus the JavaFX main class path. |
| [L15](https://github.com/ceias/minions/blob/dba2d321e276383939b0bf661659738741bdd343/MiniOS-Java/javafx/pom.xml#L15) | <code>      &lt;plugin&gt;&lt;groupId&gt;org.apache.maven.plugins&lt;/groupId&gt;&lt;artifactId&gt;maven-surefire-plugin&lt;/artifactId&gt;&lt;version&gt;3.5.2&lt;/version&gt;&lt;configuration&gt;&lt;systemPropertyVariables&gt;&lt;glass.platform&gt;Monocle&lt;/glass.platform&gt;&lt;monocle.platform&gt;Headless&lt;/monocle.platform&gt;&lt;headless.geometry&gt;1600x1200-32&lt;/headless.geometry&gt;&lt;javafx.cachedir&gt;${project.build.directory}/javafx-cache&lt;/javafx.cachedir&gt;&lt;prism.order&gt;sw&lt;/prism.order&gt;&lt;/systemPropertyVariables&gt;&lt;/configuration&gt;&lt;/plugin&gt;</code> | Run JUnit with Surefire3.5.2; test-only properties choose Monocle Headless, a1600×1200 virtual screen, writable JavaFX cache and software rendering. |
| [L16](https://github.com/ceias/minions/blob/dba2d321e276383939b0bf661659738741bdd343/MiniOS-Java/javafx/pom.xml#L16) | <code>      &lt;plugin&gt;&lt;groupId&gt;org.openjfx&lt;/groupId&gt;&lt;artifactId&gt;javafx-maven-plugin&lt;/artifactId&gt;&lt;version&gt;0.0.8&lt;/version&gt;&lt;configuration&gt;&lt;mainClass&gt;MiniOSFX&lt;/mainClass&gt;&lt;/configuration&gt;&lt;/plugin&gt;</code> | Pin JavaFX plugin0.0.8 and make MiniOSFX the main class used by javafx:run. |
| [L17](https://github.com/ceias/minions/blob/dba2d321e276383939b0bf661659738741bdd343/MiniOS-Java/javafx/pom.xml#L17) | <code>    &lt;/plugins&gt;</code> | End plugin declarations. |
| [L18](https://github.com/ceias/minions/blob/dba2d321e276383939b0bf661659738741bdd343/MiniOS-Java/javafx/pom.xml#L18) | <code>  &lt;/build&gt;</code> | End build configuration. |
| [L19](https://github.com/ceias/minions/blob/dba2d321e276383939b0bf661659738741bdd343/MiniOS-Java/javafx/pom.xml#L19) | <code>&lt;/project&gt;</code> | End the Maven project. |
