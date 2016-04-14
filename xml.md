继承的世界
> 在面向对象的世界中，继承是作为一种子类能自动的按照一定条件享用父类的属性和方法的一种机制，这种机制往往是作用于类的基础之上的。在某些现代编程语言中，类也是或者类似于一个对象，因此继承的概念也可以认为是在某个类对象上的操作，即在父类对象基础上进行增加、修改父类对象的属性和方法，这种继承比较典型的实现是JavaScript脚本语言中的基于原型的继承。
> 按照传统的继承的模型，假设存在三个类Class A、Class B、Class C ，其中Class A是Class B的父类、Class B是Class C的父类。则各类之间，属性则是Class B会比Class A多，Class C 会比Class B 多。

于此同时，在实现Class B 时没有重写Class A的所有的东西，而且直接取来使用或修改，实现Class C时也是直接从Class B处取得其拥有的属性和方法，而不管Class B是如何拥有这些信息，或是将这些信息在Class C里重新定义一遍。当然重新定义父类的属性和方法，那也不是我们通常所说的继承了。
什么是XML继承
> 尝试将这个继承的概念引入到XML文档中来，我们可以发现在其中也能有良好的应用前景。众所周知，每个XML文档都应该有其定义（DTD或XML Schema），和类的继承类似，对XML的定义（DTD或XML Schema）按照继承的定义对多个定义执行父子关联，也可以得到和类继承类似的继承树。
> 但是这种XML定义继承存在一个巨大的问题，即如何表示继承、修改的是哪部分。在类的继承中，每个属性或者方法都有明确的标示，在子类中可以据此明确的继承或者覆盖掉父类的相关定义。但是在XML的定义里缺少这种表示，即很难给程序指出你要继承或覆盖掉哪部分。
> 既然XML的定义很难在现有的标准上实现继承，那尝试按照对象的继承来对具体的XML文档来进行类似的继承的操作，结果会如何呢？幸运的是，在W3C的XML系列规范中，有一个叫XPath的规范明确的定义了如何访问一个既有的XML文档里的任何一个部分（节点）。按照类继承的定义，首先存在一个父文档，然后需要得到一个子文档，在子文档里可以直接继承父文档已经定义的内容，并据此为基础上对实现继承相关的增加、修改等操作，并且具体修改的过程和方法应该是规范的、可描述的，则称这种行为是XML继承。
XML继承的实现
> 在实现XML继承时，参考类继承的要素，需要强调三个要素：
1.	操作：在类继承模式，继承的主要操作是覆盖和增加新的属性。在XML继承时，考虑提供删除指定XML节点的功能。当前主要考虑的是Element、Attribute和Text节点的增删改操作，不考虑对注释、命令等节点的操作。
2.	节点的引用：在类继承中，子类方法的覆盖实现中可以引用父类该方法的实现，也可以直接调用父类或者子类的其他方法。XML继承中，定义新节点时也可以直接引用父文档或者子文档中的已有节点，同时支持对节点的重新定义。
3.	继承变化规则的记录：在类继承中，继承、变化的内容是通过子类作为继承变化信息保存的实体的。在XML继承中，由于子文档在具体的应用中需要是作为最终的信息来解析，并被其他的应用程序所使用，因此考虑将变化信息记录在特定的文件中，该文件最好也是XML格式的。
因此，在实际的XML实现中，需要有一个作为父文档的原始文档，另外还有一个记录继承、变化信息的XML文档（称之为继承定义文档）；在继承定义文档中，记录了具体的继承操作的各项操作、操作的旧有节点以及作为替换值的新节点的定义；在新对象的定义中，可以引用父文档中的已有节点,甚至是新定义的节点；最后，根据原始文档和继承定义文档，可以得到最终的子文档，以满足实际的使用需要。
应用与发展展望
在实际的应用中，XML继承可以作为一种重要的模板的技术来使用。以前的模板技术，基本都是在模板中定义了基本的展示样式，并且在模板文件中通过脚本等方式来定义数据如何输出数据。应用XML继承，可以定义单独的默认的输出模板，同时通过将数据和改变的位置定义在继承定义文档中，或是只将定义放置于继承定义文档中，而将数据作为引用节点，则即可以得到适应最新数据的最终的文档，也实现了展示模板和具体展示规则、数据的分离。
在未来的发展上，可以考虑将上文的提及关于XML定义的继承和XML继承结合，以期得到能力更强的XML模板技术。

张爱雪 于2008年7月25日晚

更多信息请参见http://blog.sina.com.cn/s/blog\_5749ead90100a3za.html