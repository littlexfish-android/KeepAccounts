package org.lf.android.keepaccounts.io

import java.lang.StringBuilder

class DataHandler {
	
	private var timeStamp = 0L
	private var tag: String
	private var remark: String
	private val isDetail: Boolean
	
	var data: Detail
	
	constructor(toSave: Detail, timeS: Long, tag: String, remark: String) {
		data = toSave
		timeStamp = timeS
		this.tag = tag
		this.remark = remark
		isDetail = true
	}
	
	constructor(total: Int, timeS: Long, tag: String, remark: String) {
		data = Detail(MoneyType.One, total)
		timeStamp = timeS
		this.tag = tag
		this.remark = remark
		isDetail = false
	}
	
	fun getTimeStamp(): Long {
		return timeStamp
	}

	fun isDetail() = isDetail

	fun getTagString() = tag
	
	fun getRemark() = remark
	
	class Detail {
		
		var twoThousand = 0
		var thousand = 0
		var fiveHundred = 0
		var twoHundred = 0
		var hundred = 0
		var fifty = 0
		var twenty = 0
		var ten = 0
		var five = 0
		var one = 0
		
		constructor(twoTh: Int, th: Int, fiveHun: Int, twoHun: Int, hun: Int, fif: Int, twe: Int, t: Int, f: Int, o: Int) {
			setAllDetail(twoTh, th, fiveHun, twoHun, hun, fif, twe, t, f, o)
		}
		
		constructor(th: Int, fiveHun: Int, hun: Int, fif: Int, t: Int, f: Int, o: Int) {
			setAllDetail(0, th, fiveHun, 0, hun, fif, 0, t, f, o)
		}
		
		constructor() {
		
		}
		
		constructor(type: MoneyType, value: Int) {
			setDetail(type, value)
		}
		
		private fun setAllDetail(twoTh: Int, th: Int, fiveHun: Int, twoHun: Int, hun: Int, fif: Int, twe: Int, t: Int, f: Int, o: Int) {
			twoThousand = twoTh
			thousand = th
			fiveHundred = fiveHun
			twoHundred = twoHun
			hundred = hun
			fifty = fif
			twenty = twe
			ten = t
			five = f
			one = o
		}
		
		fun setDetail(type: MoneyType, value: Int): Detail {
			when(type) {
				MoneyType.TwoThousand -> twoThousand = value
				MoneyType.Thousand -> thousand = value
				MoneyType.FiveHundred -> fiveHundred = value
				MoneyType.TwoHundred -> twoHundred = value
				MoneyType.Hundred -> hundred = value
				MoneyType.Fifty -> fifty = value
				MoneyType.Twenty -> twenty = value
				MoneyType.Ten -> ten = value
				MoneyType.Five -> five = value
				MoneyType.One -> one = value
			}
			return this
		}
		
		fun count(): Int {
			var total = 0
			total += (twoThousand * 2000) + (thousand * 1000) + (fiveHundred * 500)
			total += (twoHundred * 200) + (hundred * 100) + (fifty * 50)
			total += (twenty * 20) + (ten * 10) + (five * 5) + (one * 1)
			return total
		}
		
	}
	
	enum class MoneyType {
		TwoThousand, Thousand, FiveHundred, TwoHundred,
		Hundred, Fifty, Twenty, Ten, Five, One;
	}
	
}