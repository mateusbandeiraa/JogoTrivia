package br.uniriotec.bsi.jogotrivia.service;

public class Views {
	public static interface View{};
	public static class ViewPublico implements View{};
	public static class ViewAutenticado extends ViewPublico implements View{};
	public static class ViewAnfitriao extends ViewPublico implements View{};
	public static class ViewRodadaAberta extends ViewAutenticado implements View{};
	public static class ViewRodadaEncerrada extends ViewRodadaAberta implements View{};
	public static class ViewHistorico implements View{};
}
