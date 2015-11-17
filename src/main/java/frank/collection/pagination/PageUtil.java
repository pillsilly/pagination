package frank.collection.pagination;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import frank.collection.pagination.bean.Page;


public class PageUtil {
	static final Logger logger = Logger.getLogger(PageUtil.class);
	/**
	 * 处理传入数字为字符串的情况
	 *
	 * @param sPageno
	 * @param sPageRows
	 * @param sRowsAll
	 * @param paramMap
	 * @return
	 */
	public static final Page getPage(String sPageno, String sPageRows, String sRowsAll ) {
		//default
		Page page = new Page();
		// 当前页号
		Long pageno = page.getPageno();
		// 每页显示条数
		Long pagerows = page.getPagerows();
		// 总记录数
		Long rowsall = page.getRowsall();
		try {
			// begin---------处理页数
			// 每页多少行
			if (StringUtils.isNumeric(sPageRows)) {
				pagerows = new Long(sPageRows);
			}
			// 当前页
			if (StringUtils.isNumeric(sPageno)) {
				pageno = new Long(sPageno);
			}
			// 总页数
			if (StringUtils.isNumeric(sRowsAll)) {
				rowsall = new Long(sRowsAll);
			}
		} catch (Exception e) {
			logger.warn(e.getLocalizedMessage());
			throw new IllegalArgumentException(e);
		}
		return PageUtil.getPage(pageno, pagerows, rowsall );
	}

	/**
	 * 计算page对象
	 *
	 * @param pageno
	 * @param pagerows
	 * @param rowsall
	 * @return
	 */
	public static final Page getPage(Long pageno, Long pagerows, Long rowsall ) {
		Page page = new Page();
		Long pagenoall = 1L;
		// begin---------处理页数
		if (rowsall == null || rowsall.longValue() <= 0) {
			rowsall = 0L;
		}
		// 计算当前页，不能超过限制
		if (rowsall.longValue() <= pagerows.longValue()) {
			pageno = 1L;
		} else {
			// 计算总页数
			long allpagenoTemp = rowsall.longValue() / pagerows.longValue();
			if (rowsall.longValue() % pagerows.longValue() != 0) {
				// 如果不是整数页，就多一页
				allpagenoTemp = allpagenoTemp + 1;
			}
			pagenoall = allpagenoTemp;
		}
		// 当前页不能超过最大页
		if (pageno.longValue() > pagenoall.longValue()) {
			pageno = pagenoall;
		}
		page.setPageno(pageno);
		page.setPagerows(pagerows);
		page.setPagenoall(pagenoall);
		page.setRowsall(rowsall);
		return page;
	}

	public static void main(String[] args) {
		Page page = PageUtil.getPage("10", "6", "100");
		System.out.println(page.getPageno());
		System.out.println(page.getPagerows());
		System.out.println(page.getPagenoall());
		System.out.println(page.getRowsall());
	}
}
