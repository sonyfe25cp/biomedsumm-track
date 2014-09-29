biomedsumm-track
================

###数据中d问题

1. 分句

	采用先按行读入,然后给每行最后都加一个.
	然后用stanford分句,句子切分比较准确.
	
2. 句子标注: 把rp中的句子标注取出

	先把rp全文分句
	然后用Jacard相似度来判断是否是同一个句子
	

###task1a方法

1. 先根据不同人对同一条引用的分析，来分析如何从RP中找出来合适的句子作为答案。

分析：

	1)对比RP中所有句子,进行聚类分析;
	
	2)将RP中所有句子,进行标注:1为被引用,-1为没有被引用.进行分类模型训练;看准确率

	3)计算CP的句子与RP中所有句子相关度
		
		实验证明:cp的句子跟引用的rp句子上没有相似度的直接关系,大部分的cosine相似度都很低.
		FindRelationsByCompare.compareLabel	
		
		key words?
	


1. 对同一个CP，拿出所有CP->RP,标示 正例和负例，形成pair

2. 对每一组抽取特征

	a. 长度的abs
	b. 共同词的个数 / Jaccard相似度
	c. cos距离
	
	distance = lambda f1 + lambda f2 + lambda cos(A,B) 
	
3. 损失函数 f = d(正例) + \sum frac{1}{d(负例)}




###task1b方法

1. 根据已经有的句子与标注之间的关系，分析是哪些词起到了决定性作用。

	1) 把所有同类facet的句子放在一起, 训练分类模型

###task2方法

1. 根据同一个人对所有CP和RP的抽取，分析其摘要与这些span的关系，生成一个权重判断函数。


###提交
cm： 用cosine 计算相似度，取最大的
tp3: 用cosine取前3
tp5： 用cosine 取前5

label：bayes相似度计算

