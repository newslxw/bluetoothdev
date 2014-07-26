package com.alensic.nursing.mobile.model;

/**
 * 血压数据
 * @author xwlian
 *
 */
public class Blood {
	private int dia;//收缩压
	private int sys;//舒张压
	private int map;//MAP
	private int mb;//脉搏
	public int getDia() {
		return dia;
	}
	public void setDia(int dia) {
		this.dia = dia;
	}
	public int getSys() {
		return sys;
	}
	public void setSys(int sys) {
		this.sys = sys;
	}
	public int getMap() {
		return map;
	}
	public void setMap(int map) {
		this.map = map;
	}
	public int getMb() {
		return mb;
	}
	public void setMb(int mb) {
		this.mb = mb;
	}
	@Override
	public boolean equals(Object o) {
		if(o==null||!(o instanceof Blood))return false;
		Blood bo = (Blood)o;
		return bo.getDia()==dia&&bo.getMap()==map&&bo.getMb()==mb&&bo.getSys()==sys;
	}
	
	
	
}
