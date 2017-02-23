package br.cefet.eic.selenium.kanggle;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import br.cefet.eic.selenium.kanggle.model.DataSetModel;
import junit.framework.Assert;

public class SeleniumKaggleTest {
	ChromeDriver wd;
	private List<DataSetModel> datasets;
	private Long numTotalItens = 0L;
	private int paginaAtual = 1;
	private static final String ARQUIVO_CSV = "C:\\tmp\\kaggle.csv";

	@Before
	public void setUp() throws Exception {
		File file = new File("C:\\Windows\\System32\\chromedriver.exe");
		System.setProperty("webdriver.chrome.driver", file.getAbsolutePath());
		wd = new ChromeDriver();
		datasets = new ArrayList<DataSetModel>();
	}

	@Test
	public void execute() {

		try {
			buscarDatasets();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		int count = 0;
		while (count < this.datasets.size()) {
			DataSetModel dataSet = this.datasets.get(count);
			try {
				wd.manage().timeouts().implicitlyWait(60, TimeUnit.SECONDS);
				wd.get(dataSet.getUrl());
				String subtitle = wd.findElementByClassName("dataset-header__subtitle").getText();
				String size = wd.findElementByClassName("file-preview__meta-value").getText();
				dataSet.setSubtitle(subtitle);
				dataSet.setSize(size);
				String[] splitSize = size.split(" ");
				if (splitSize.length > 0) {
					dataSet.setSizeUnit(splitSize[1]);
				}
				System.out.println("DataSet Size: " + dataSet.getSize());
				System.out.println("DataSet Size Unit: " + dataSet.getSizeUnit());
				System.out.println("DataSet Subtitle: " + dataSet.getSubtitle());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			count++;
		}
		
		escreveArquivo();

	}

	@After
	public void tearDown() {
		// wd.quit();
	}

	public static boolean isAlertPresent(FirefoxDriver wd) {
		try {
			wd.switchTo().alert();
			return true;
		} catch (NoAlertPresentException e) {
			return false;
		}
	}

	private String executarConsultaJSON(int pagina) throws Exception {
		String url = "https://www.kaggle.com/datasets.json?sortBy=hottest&group=all";
		if (pagina > 1) {
			url += "&page=" + String.valueOf(pagina);
		}
		URL urlObj = new URL(url);
		HttpURLConnection httpConnection = (HttpURLConnection) urlObj.openConnection();
		httpConnection.setDoOutput(false);
		httpConnection.setDoInput(true);
		httpConnection.setRequestMethod("GET");
		httpConnection.connect();
		InputStream inputStream = httpConnection.getInputStream();
		InputStreamReader reader = new InputStreamReader(inputStream);
		BufferedReader buffReader = new BufferedReader(reader);
		StringBuilder retorno = new StringBuilder();
		String line = "";
		while ((line = buffReader.readLine()) != null) {
			retorno.append(line);
		}
		return retorno.toString();
	}

	private void buscarDatasets() throws Exception {
		String dados = executarConsultaJSON(paginaAtual);
		System.out.println(dados);
		JsonElement root = new JsonParser().parse(dados);
		JsonArray arr = root.getAsJsonObject().getAsJsonArray("datasetListItems");
		this.numTotalItens = root.getAsJsonObject().get("totalDatasetListItems").getAsLong();
		for (JsonElement elem : arr) {
			DataSetModel dataSet = new DataSetModel();
			dataSet.setTitle(elem.getAsJsonObject().get("title").getAsString());
			dataSet.setUrl("https://www.kaggle.com" + elem.getAsJsonObject().get("datasetUrl").getAsString());
			this.datasets.add(dataSet);
		}
		if(this.numTotalItens > this.datasets.size()) {
			this.paginaAtual++;
			buscarDatasets();
		}
	}

	private void escreveArquivo() {
		BufferedWriter buffWriter = null;
		try {
			File file = new File(ARQUIVO_CSV);
			if (file.exists()) {
				file.delete();
			}
			file.createNewFile();
			FileWriter writer = new FileWriter(file);
			buffWriter = new BufferedWriter(writer);
			buffWriter.write("Titulo;Subtitulo;Tamanho;Unidade;URL");
			buffWriter.newLine();
			int count = 0;
			while (count < this.datasets.size()) {
				DataSetModel dataSet = this.datasets.get(count);
				buffWriter.write(dataSet.toString());
				buffWriter.newLine();
				count++;
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			try {
				if (buffWriter != null)
					buffWriter.close();
			} catch (IOException e) {
			}
		}
	}

}
