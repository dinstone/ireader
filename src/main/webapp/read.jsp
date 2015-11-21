<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>${article.name}${part.name}</title>
</head>
<body>
	<c:set var="contextPath" value="${pageContext.request.contextPath}" />
	<table width="900px" align="center" cellpadding="0" cellspacing="0">
		<tbody>
			<tr>
				<td>
					<div class="Header">
						<c:forEach items="${categorys}" var="category">
							<a href="${contextPath}/view/article/category/${category.id}-1">${category.name}</a> |
						</c:forEach>
						<a href="${contextPath}/view/article/list/1">排行榜</a>
					</div>
				</td>
			</tr>
		</tbody>
	</table>
	<table width="900px" align="CENTER" cellpadding="0" cellspacing="0">
		<tr>
			<td>${article.name}</td>
			<td>作者:${article.auth}</td>
			<td>分类:${article.category}</td>
			<td>第${pagenation.current}节</td>
			<td><a href="${contextPath}/view/article/download/${article.id}">下载TEXT</a></td>
		</tr>
		<tr>
			<td colspan="5"><p>${content}</p></td>
		</tr>
		<tr>
			<td></td>
			<c:if test="${!empty pagenation.prev}">
				<td><a
					href="${contextPath}/view/article/read/${article.id}-${pagenation.prev}">［上一节］</a></td>
			</c:if>
			<c:if test="${empty pagenation.prev}">
				<td></td>
			</c:if>
			<td>第${pagenation.current}/${pagenation.total}节</td>
			<c:if test="${!empty pagenation.next}">
				<td><a
					href="${contextPath}/view/article/read/${article.id}-${pagenation.next}">［下一节］</a></td>
			</c:if>
			<c:if test="${empty pagenation.next}">
				<td></td>
			</c:if>
			<td></td>
		</tr>
	</table>
	<table width="90%" align="center" cellpadding="3" cellspacing="0"
		border="0">
		<tbody>
			<tr>
				<td align="center">© CopyRight 2015
					爱易读所有作品由自动化设备收集于互联网.作品各种权益与责任归原作者所有.</td>
			</tr>
		</tbody>
	</table>
</body>
</html>