package frank.collection.pagination;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import frank.collection.pagination.bean.Page;


public class PageUtil {
	static final Logger logger = Logger.getLogger(PageUtil.class);
	/**
	 * ����������Ϊ�ַ��������
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
		// ��ǰҳ��
		Long pageno = page.getPageno();
		// ÿҳ��ʾ����
		Long pagerows = page.getPagerows();
		// �ܼ�¼��
		Long rowsall = page.getRowsall();
		try {
			// begin---------����ҳ��
			// ÿҳ������
			if (StringUtils.isNumeric(sPageRows)) {
				pagerows = new Long(sPageRows);
			}
			// ��ǰҳ
			if (StringUtils.isNumeric(sPageno)) {
				pageno = new Long(sPageno);
			}
			// ��ҳ��
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
	 * ����page����
	 *
	 * @param pageno
	 * @param pagerows
	 * @param rowsall
	 * @return
	 */
	public static final Page getPage(Long pageno, Long pagerows, Long rowsall ) {
		Page page = new Page();
		Long pagenoall = 1L;
		// begin---------����ҳ��
		if (rowsall == null || rowsall.longValue() <= 0) {
			rowsall = 0L;
		}
		// ���㵱ǰҳ�����ܳ�������
		if (rowsall.longValue() <= pagerows.longValue()) {
			pageno = 1L;
		} else {
			// ������ҳ��
			long allpagenoTemp = rowsall.longValue() / pagerows.longValue();
			if (rowsall.longValue() % pagerows.longValue() != 0) {
				// �����������ҳ���Ͷ�һҳ
				allpagenoTemp = allpagenoTemp + 1;
			}
			pagenoall = allpagenoTemp;
		}
		// ��ǰҳ���ܳ������ҳ
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
