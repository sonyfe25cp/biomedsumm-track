package model;

import java.util.List;

/**
 * 目的是观察不同人对同一个引用的看法，来判断如何找出合适的引用段。
 * @author Sonyfe25cp
 *
 * 2014年6月24日
 */
public class PaperInstance {

	public int id;
	public String citantionMarker;
	public Paper RP;
	public List<Citantion> citantions;
	
	
}
