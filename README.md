# Tree of Usages (Recursive usages) - Java

Forked from [RPGer/Recursive-usages](https://github.com/RPGer/Recursive-usages)

This IntelliJ plugin helps to recursively find all usages of a Java method and creates a tree of those usages.
When changing a method, this allows you to see what other methods it will affect.

### To Build
From this directory, run
<br/>`./gradlew build` (*nix)
<br/>`gradlew.bat build` (Windows)

### To Install
![Install From Disk](img/install-from-disk.png)
1. Navigate to Settings > Plugins
2. Choose "Install Plugin from Disk..."
3. Select `Recursive-usages-Java/build/distributions/Recursive-usages-Java-2.0.zip`
4. Restart IntelliJ and wait for indexing to complete

### To Use
![Plugin in Action](img/plugin-in-action.png)
1. Select a method
2. Click green play button
3. ...
4. PROFIT!

### Support
* Standard instance method usages
* Usages as method references
* Static method usages
* Method usages in static initialization blocks
* Method usages in field initializations
* Usages of immediate parents of method
* Usages of immediate children of method

Does not yet support usages of methods in declarative contexts, like SpEL and XML. No Lombok support.

### Feel free to provide feedback or report a bug