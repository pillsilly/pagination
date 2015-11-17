package frank.collection.pagination;

import frank.collection.pagination.bean.Page;
import frank.collection.pagination.bean.PageTag;
import frank.collection.pagination.bean.Tag;
import frank.collection.servlet.ServletContextUtil;



public class PageTagUtil {
	/**
	 * ��������
	 *
	 * @param url
	 *            "http://website/module.jsp?a=1&b=2" ����
	 *            "http://website/module.jsp?pagenow=1&pagerows=2" ����
	 *            "http://website/module.jsp"
	 * @param pageno
	 * @param pagerows
	 * @return
	 */
	protected static String formatUrlByPageno(String url, long pageno, long pagerows) {
		String connectStr = "&";
		if (url.indexOf("?") > -1) {
			url = url.substring(0, url.indexOf("?") + 1) + ServletContextUtil.removeParamInURL(url.substring(url.indexOf("?") + 1), new String[] { Page.PAGENO, Page.PAGEROWS });
		} else if (url.indexOf("?") == -1) {
			connectStr = "?";
		}
		return url + connectStr + Page.PAGENO + "=" + pageno + "&" + Page.PAGEROWS + "=" + pagerows;
	}
	
	
	/**
	 * public static void main(String[] args) { <br>
	 * Page page = PageUtil.calcPage("1", "3", "61" );<br>
	 * System.out.println(" ��ǰ :"+page.getPageno());<br>
	 * System.out.println(" ��ҳ�� :"+page.getPagenoall());<br>
	 * System.out.println(" �ܼ�¼�� :"+page.getRowsall());<br>
	 * System.out.println(" ÿҳ :"+page.getPagerows());<br>
	 * System.out.println("��ʼ����ҳ��");<br>
	 * PageTag pageTag = PageTagUtil.getPageTags(page, "testurl", 5 );<br>
	 * System.out.println(pageTag);<br>
	 * }<br>
	 * 
	 * @param page
	 * @param url
	 * @param tagno
	 * @return
	 */
	public static PageTag getPageTags(Page page, String url, int tagno ) {
		PageTag pageTag = new PageTag();
		long currentNum = page.getPageno();
		long totalNum = page.getPagenoall();
		long pagerows = page.getPagerows();
		if (totalNum < tagno) {
			// ������ҳ����Ҫ�ٵ�ʱ��
			for (int i = 1; i <= totalNum; i++) {
				Tag tag = new Tag();
				tag.setCurrent(currentNum == i);
				tag.setValue(i);
				tag.setUrl(formatUrlByPageno(url, i, pagerows));
				pageTag.getList().add(tag);
			}
			PageTagUtil.setParticular(url, page, pageTag);
			// ���,�˳�
			return pageTag;
		}
		// ����ƫ��ֵ,����ǻ���,��ôΪҳ����/2+1 �����ż��,��ô��ҳ����/2
		int offset = 0;
		if (tagno % 2 != 0) {
			offset = (tagno / 2) + 1;
		} else {
			offset = (tagno / 2);
		}
		// ��ǰҳС����ҳ�����Ҵ���0
		if (currentNum > 0 && currentNum <= totalNum) {
			// ��ǰҳ����ǰNҳ��,N=offsetƫ��ֵ
			if (currentNum < offset) {
				for (int i = 1; i <= tagno; i++) {
					Tag tag = new Tag();
					tag.setCurrent(currentNum == i);
					tag.setValue(i);
					tag.setUrl(formatUrlByPageno(url, i, pagerows));
					pageTag.getList().add(tag);
				}
			} else if (currentNum > (totalNum - offset)) {
				// ��ǰҳ���ں�Nҳ��,N=offsetƫ��ֵ
				for (long i = (totalNum - tagno) + 1; i <= totalNum; i++) {
					Tag tag = new Tag();
					tag.setCurrent(currentNum == i);
					tag.setValue(new Long(i).intValue());
					tag.setUrl(formatUrlByPageno(url, i, pagerows));
					pageTag.getList().add(tag);
				}
			} else {
				// ��ǰҳ�����м�ҳ��,�ӵ�tagno-offset��ҳ��������tagnoҳ
				for (int i = 1; i <= tagno; i++) {
					Tag tag = new Tag();
					tag.setCurrent((currentNum - offset + i) == currentNum);
					tag.setValue(new Long((currentNum - offset + i)).intValue());
					tag.setUrl(formatUrlByPageno(url, currentNum - offset + i, pagerows));
					pageTag.getList().add(tag);
				}
			}
		}
		PageTagUtil.setParticular(url, page, pageTag);
		return pageTag;
	}

	protected static void setParticular(String url, Page page, PageTag pageTag) {
		Tag prev = new Tag();
		Tag next = new Tag();
		Tag first = new Tag();
		Tag last = new Tag();
		Tag current = new Tag();
		if (page.getPageno() > 2) {
			prev.setValue(page.getPageno().intValue() - 1);
			prev.setUrl(formatUrlByPageno(url, page.getPageno().intValue() - 1, page.getPagerows()));
		}
		if (page.getPageno() <= 2) {
			prev.setValue(1);
			prev.setUrl(formatUrlByPageno(url, 1, page.getPagerows()));
		}
		if (page.getPageno() >= page.getPagenoall()) {
			next.setValue(page.getPagenoall().intValue());
			next.setUrl(formatUrlByPageno(url, page.getPagenoall().intValue(), page.getPagerows()));
		} else if (page.getPageno() + 1 <= page.getPagenoall()) {
			next.setValue(page.getPageno().intValue() + 1);
			next.setUrl(formatUrlByPageno(url, page.getPageno().intValue() + 1, page.getPagerows()));
		}
		first.setValue(1);
		first.setUrl(formatUrlByPageno(url,1, page.getPagerows()));
		last.setValue(page.getPagenoall().intValue());
		last.setUrl(formatUrlByPageno(url, page.getPagenoall(), page.getPagerows()));
		if(page.getPageno().intValue()>first.getValue()&&page.getPageno().intValue()<last.getValue()){
			current.setValue(page.getPageno().intValue());
			current.setUrl(formatUrlByPageno(url, page.getPageno(), page.getPagerows()));
		}else
		if(page.getPageno()<=first.getValue()){
			current.setValue(first.getValue());
			current.setUrl(formatUrlByPageno(url, first.getValue(), page.getPagerows()));
		}else
		if(page.getPageno()>=last.getValue()){
			current.setValue(last.getValue());
			current.setUrl(formatUrlByPageno(url, last.getValue(), page.getPagerows()));
		}
		pageTag.setCurrent(current);
		pageTag.setFirst(first);
		pageTag.setLast(last);
		pageTag.setPrev(prev);
		pageTag.setNext(next);
	}


	

}
