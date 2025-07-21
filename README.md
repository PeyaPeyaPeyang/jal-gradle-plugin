<div align="center">
  <h1>JAL</h1>
</div>

**JAL (Java Assembly Language)**  is a custom-designed assembly language for the Java Virtual Machine.  
It makes exploring the internals of the JVM not just possible, but genuinely fun.

---

## 🚀 What is JAL?

JAL is a fresh take on Java assembly — a spiritual successor to projects like **Jasmin**, but with modern features and design.

Unlike Jasmin, which stopped development around 2022 and only supports outdated Java versions, **JAL supports modern JVM features**, including automatic generation of **StackMapFrames**, which are mandatory from Java 1.6 and up. Jasmin users may remember `VerifyError`s haunting their dreams — JAL eliminates them.

### Why JAL is cool:

- **Named local variables**  
  Instead of remembering slot numbers, you can name your variables:
  ```
  istore 0 [->example]  
  iload example
  ```
- **Structured exception handling with labels**  
  Try-catch-finally blocks are declared with jump labels:
  ```
  tryStart: [~tryEnd, java/lang/Exception: catchStart, java/lang/Error: catchStart2 ->finallyStart]  
  tryEnd:  
    
  catchStart:  
  catchStart2:  
  finallyStart:
   ```

- **Readable member references**  
  Method and field calls separate class and member names for clarity:
  ```
  invokevirtual java/io/PrintStream->println(Ljava/lang/String;)V
  ```

### Sample: HelloWorld

```java
public class HelloWorld {
  public static main([Ljava/lang/String;)V {
    // Print "Hello, World!"
    getstatic java/lang/System->out:Ljava/io/PrintStream;
    ldc "Hello, World!"
    invokevirtual java/io/PrintStream->println(Ljava/lang/String;)V
    
    // Return from main
    return
  }
}
```

---

## 🧠 StackMapFrame? Automatically Done.

One of the biggest pain points in JVM bytecode writing is StackMapFrame management.  
JAL's compiler **calculates and inserts StackMapFrames automatically** — no more manual frame declaration, no more hair-pulling `VerifyError`s.

---

## 📦 Installation

JAL is available as a [Javasm IntelliJ plugin](https://plugins.jetbrains.com/plugin/27944-javasm), [jal-gradle-plugin](https://github.com/PeyaPeyaPeyang/jal-gradle-plugin)
or [JAL CLI Compiler](https://github.com/PeyaPeyaPeyang/LangJAL/releases)

---

## 🛠️ JAL Gradle plugin Usage

1. Apply the plugin:
  ```groovy
  plugin {
    id 'tokyo.peya.langjal' version 0.0.1
  }
  ```
2. Writing source codes in directory: `src/main/jal`
3. Build a project: `./gradlew build` or `./gradlew compileJAL` 
4. Output classes will be appeared in `build/classes/jal/` and a .jar file will be in `build/libes`

---

## 🔌 Javasm IntelliJ Plugin Features

Javasm supercharges your JAL development inside IntelliJ IDEA with [Javasm](https://plugins.jetbrains.com/plugin/27944-javasm) plugin:

- ✅ **Instruction name completion**  
  Start typing and get autocompletion for all instructions.

- 📄 **Hover documentation**  
  Hover over instructions to see inline documentation.

- 🌀 **Label navigation**  
  Ctrl+Click a label to jump to its declaration.

- 🐞 **Debugger integration**  
  Full integration with IntelliJ's standard JVM debugger (JDWP).  
  Breakpoints, step-over, step-into — all supported.

- 📊 **Frame and Stack Viewer**   
  A custom tool window that shows:
  - Stack state at the selected instruction
  - Local variable states
  - Visualises the stack during live debugging sessions

---

## 📄 License

MIT License. See the [LICENCE](./LICENCE) file for details.

---

❤️ A project by someone who enjoys poking around the JVM bytecode guts.
