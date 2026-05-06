
##  How to Run

### Prerequisites
1. **JDK 22 or higher** (Ensure your JAVA_HOME is set correctly).
2. **JavaFX SDK**: Download the SDK from [Gluon](https://gluonhq.com/products/javafx/).

### Setup in Eclipse
1. **Import the Project**: Clone this repo and import it as a Java Project.
2. **Add JavaFX to Build Path**:
   - Right-click Project > Properties > Java Build Path > Libraries.
   - Add the JavaFX JARs from your SDK `lib` folder to the **Classpath**.
3. **Configure VM Arguments**:
   - Go to Run Configurations > Arguments.
   - Paste the following in the **VM Arguments** box (update the path to your actual SDK):
     ```text
     --module-path "C:\path\to\javafx-sdk\lib" --add-modules javafx.controls,javafx.fxml
     ```
4. **Run**: Launch `Main.java`.

---

## Controls
- **W**: Move Up
- **A**: Move Left
- **S**: Move Down
- **D**: Move Right

---

## Built With
- **Java**
- **JavaFX** (Canvas & AnimationTimer)

---

## Preview
*Insert a screenshot of your game here once it's running!*