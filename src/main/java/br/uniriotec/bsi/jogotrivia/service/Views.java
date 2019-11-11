package br.uniriotec.bsi.jogotrivia.service;

public class Views {
	public static interface View{};
	public static class ViewPublico implements View{};
	public static class ViewAutenticado extends ViewPublico implements View{};
	public static class ViewIngame extends ViewPublico implements View{};
	public static class ViewAnfitriao extends ViewPublico implements View{};
}
