package projekt;


import calc.Body;
import dataBase.DataBase;
import drawing.DrawablePanel;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadLocalRandom;
import javax.swing.*;
import javax.swing.border.TitledBorder;


import static java.util.concurrent.TimeUnit.MILLISECONDS;


public class GUI extends JFrame {

	private static final long serialVersionUID = 1L;
	static final int WIDTH=1280, HEIGHT=720;
	private int selectedBody = 0; //body selected by JComboBox, by default it will show first body
	private  int iPanel=0;
	private int numSimulatedBody = 2; //keep number of simulated body coming from pNumberSpinner, by default we show only 2 bodies
	private int simulationTime=15;  //default = 15
	public boolean STOPSIMULATION = false;
	private boolean StopPushed = false;

	JPanel leftPanel, rightPanel;
	JPanel[] iRightPanel, paramRightPanel;
	JLabel pNumberLabel, pChoiceLabel, pMassLabel, pPosXLabel, pPosYLabel, pVXLabel, pVYLabel, simTimeLabel;
	JTextField pMassText;
	JButton startButton, endButton;
	JMenuBar menuBar;
	JMenu menuOptions, menuExamples, help;
	JMenuItem option1, option2, menuUserSettings, menuExample2, menuExample4, menuExample6, menuExample9, authors;
	JSlider sliderX, sliderY, simTimeSlider, sliderVx, sliderVy;
	Color rightPanelColor = UIManager.getColor("Panel.background"); 	//default swing color
	JSpinner pNumberSpinner;
	JComboBox<String> numberBox;
	String[] particleNumbers = {"1", "2"}; // this number will be show in numberBox at the beginning
	Body[] body; //empty array of bodies to simulation, in can change from 1 to 9 while program is working

	DrawablePanel drawablePanel;
	int dt=0;

	// variables for database system
	DataBase dataBase;
	Vector<Body> bodyListVector;

	boolean isVectorInitialize = false;

    public GUI() throws HeadlessException {
    	this.setSize(WIDTH, HEIGHT);
    	setDefaultCloseOperation(EXIT_ON_CLOSE);
    	this.setResizable(false);
    	this.setTitle("Najlepsza symulacja na swiecie");
    	this.setLocation( (int) ((Toolkit.getDefaultToolkit().getScreenSize().getWidth()-this.getWidth()) /2), 
				(int) (Toolkit.getDefaultToolkit().getScreenSize().getHeight()-this.getHeight()) /2);


    	this.setLayout(new BorderLayout());
    	leftPanel = new JPanel();
    	//leftPanel.setBackground(Color.black);
    	leftPanel.setBackground(UIManager.getColor("Panel.background"));


		//before making sliders and spinner and DrawablePanel (because we need to pass this array in constructor),
		// we have to create our bodies, by default we create 2 bodies (max we can simulate 9 bodies)
		//makeBodyArray(496, 326, 0, 0, 100);
		makeRandomBodyArray();

    	//initialize panel to draw simulation
		makeDrawablePanel();


    	//adding panels
    	rightPanel = new JPanel();
    	rightPanel.setBackground(rightPanelColor);
    	rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.PAGE_AXIS));
    	
    	this.add(leftPanel, BorderLayout.CENTER);
    	this.add(rightPanel, BorderLayout.EAST);
	 	
    	iRightPanel = new JPanel[4];
    	paramRightPanel = new JPanel[6];
    	for(int i=0; i<4; i++) {
    		iRightPanel[i] = new JPanel();
    		iRightPanel[i].setBackground(rightPanelColor);
    		rightPanel.add(iRightPanel[i]);
    	};
    	iRightPanel[iPanel+1].setLayout(new BoxLayout(iRightPanel[iPanel+1],BoxLayout.PAGE_AXIS));
    	for(int i=0; i<6; i++) {
    		paramRightPanel[i] = new JPanel();
    		paramRightPanel[i].setBackground(rightPanelColor);
    		iRightPanel[iPanel+1].add(paramRightPanel[i]);
       	};

		menuBar = new JMenuBar();
		menuOptions = new JMenu("Options");
		menuExamples = new JMenu("Examples");
		menuUserSettings = new JMenuItem("User Settings");
		menuUserSettings.addActionListener(examplesListener);
		menuExample2 = new JMenuItem("Example 2");
		menuExample2.addActionListener(examplesListener);
		menuExample4 = new JMenuItem("Example 4");
		menuExample4.addActionListener(examplesListener);
		menuExample6 = new JMenuItem("Example 6");
		menuExample6.addActionListener(examplesListener);
		menuExample9 = new JMenuItem("Example 9");
		menuExample9.addActionListener(examplesListener);
		option1 = new JMenuItem("Save simulation data");
		option1.addActionListener(e -> {
			if(!isVectorInitialize) {
				JOptionPane.showMessageDialog(null,"Nie ma danych do zapisania,\n" +
						"kliknij Start, aby stworzyc dane!");
			} else if (!STOPSIMULATION && !StopPushed) {
				JOptionPane.showMessageDialog(null, "Najpierw zatrzymaj symulacje!");
			} else {
				dataBase.saveData(
						drawablePanel.getBodyMassListVector(), drawablePanel.getBodyIDListVector(),
						drawablePanel.getxPosListVector(), drawablePanel.getyPosListVector(),
						drawablePanel.getvXListVector(), drawablePanel.getvYListVector());
			}
		});
		option2 = new JMenuItem("Load simulation data");
		option2.addActionListener(e -> {
			dataBase.loadData();
		});
		menuOptions.add(option1);
		menuOptions.add(option2);
		menuExamples.add(menuUserSettings);
		menuExamples.add(menuExample2);
		menuExamples.add(menuExample4);
		menuExamples.add(menuExample6);
		menuExamples.add(menuExample9);
		menuBar.add(menuOptions);
		menuBar.add(menuExamples);

		help = new JMenu("Help");
		menuBar.add(help);

		authors = new JMenuItem("Authors");
		authors.addActionListener(e -> {
			JOptionPane.showMessageDialog(null,
					"Autorzy:\n" +
							"Lukasz Sawicki\n" +
							"Krzysztof Palmi");
		});
		help.add(authors);
		this.setJMenuBar(menuBar);


		iRightPanel[iPanel+1].setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black) , 
				"Parameters", TitledBorder.CENTER, TitledBorder.TOP));
		pNumberSpinner = new JSpinner();
		pNumberSpinner.addChangeListener(e -> {
			numSimulatedBody = (Integer)pNumberSpinner.getValue();

			// need to set selectedBody index to 0 to make drawablePanel working properly
			selectedBody = 0;

			// array with bodies numbers to use in JComboBox
			particleNumbers = new String[numSimulatedBody];

			// new array of bodies for simulation
			//makeBodyArray(496,326,0, 0,100); 		// center in 550 and 330 but minus half oval width
			makeRandomBodyArray();
			// TODO try to rewrite old body array to new array

			// initializing new String list for JComboBox
			for(int i = 0; i < numSimulatedBody; i++) {
				particleNumbers[i] = Integer.toString(i+1);
			}

			// It is adding new list of bodies into numberBox (JComboBox)
			numberBox.setModel(new DefaultComboBoxModel<String>(particleNumbers));

			// Initialize new DrawablePanel
			leftPanel.remove(drawablePanel);
			makeDrawablePanel();

			// set all sliders with values from first body
			setSliders(0);
		});
		pNumberSpinner.setModel(new SpinnerNumberModel(2,2,9,1));

		
    	pNumberLabel = new JLabel("Number of simulated bodies:  ");
		iRightPanel[iPanel].add(pNumberLabel);
		iRightPanel[iPanel].add(pNumberSpinner);
		
		pChoiceLabel = new JLabel("Change parameters of body: ");
		numberBox = new JComboBox<String>(particleNumbers);	//LATER to change
		numberBox.addItemListener(e -> {
			try {
				selectedBody = Integer.parseInt((String) numberBox.getSelectedItem()) - 1;
			} catch (NullPointerException ex) {ex.printStackTrace();}

			//set value of each slider for selected body
			setSliders(selectedBody);
		});
		paramRightPanel[iPanel].add(pChoiceLabel);
		paramRightPanel[iPanel].add(numberBox);
	
		pMassLabel = new JLabel("Mass:  ");
		pMassText = new JTextField(Integer.toString(body[0].getMass()));
		pMassText.addActionListener(e -> {
			body[selectedBody].setMass(Integer.parseInt(pMassText.getText()));
		});
		pMassText.setColumns(3); // setting width of JTextField
		pMassText.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		paramRightPanel[iPanel+1].add(pMassLabel);
		paramRightPanel[iPanel+1].add(pMassText);
		paramRightPanel[iPanel+1].setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder(Color.black) , "Mass"));
	
		pPosXLabel = new JLabel("X:");
		sliderX = new JSlider(JSlider.HORIZONTAL,0,1000,(int)Math.round(body[0].getxPos()));	//W realu jest 900 szerokosc
		sliderX.setMajorTickSpacing(200);
		sliderX.setMinorTickSpacing(50);
		sliderX.setPaintTicks(true);
		sliderX.setPaintLabels(true);
		sliderX.addChangeListener(e -> {
			if(sliderX.getValue() <= 989) body[selectedBody].setxPos(sliderX.getValue());
			else body[selectedBody].setxPos(990); //ten smieszy warunek jest po to, zeby na sliderze bylo 1000,
			// a cialo ciagle bylo na rysunku!
		});

		pPosYLabel = new JLabel("Y:");
		sliderY = new JSlider(JSlider.HORIZONTAL,0,650,(int)Math.round(body[0].getyPos()));
		sliderY.setMajorTickSpacing(200);
		sliderY.setMinorTickSpacing(50);
		sliderY.setPaintTicks(true);
		sliderY.setPaintLabels(true);
		sliderY.addChangeListener(e -> {
			body[selectedBody].setyPos(sliderY.getValue());
		});

		pVXLabel = new JLabel("Vx:");
		sliderVx = new JSlider(JSlider.HORIZONTAL,-20,20,(int)Math.round(body[0].getvX()));
		sliderVx.setMajorTickSpacing(10);
		sliderVx.setMinorTickSpacing(5);
		sliderVx.setPaintTicks(true);
		sliderVx.setPaintLabels(true);
		sliderVx.addChangeListener(e -> {
			body[selectedBody].setvX(sliderVx.getValue());
		});

		pVYLabel = new JLabel("Vy:");
		sliderVy = new JSlider(JSlider.HORIZONTAL,-20,20,(int)Math.round(body[0].getvY()));
		sliderVy.setMajorTickSpacing(10);
		sliderVy.setMinorTickSpacing(5);
		sliderVy.setPaintTicks(true);
		sliderVy.setPaintLabels(true);
		sliderVy.addChangeListener(e -> {
			body[selectedBody].setvY(sliderVy.getValue());
		});

		//adding sliders to panel
		paramRightPanel[iPanel+2].add(pPosXLabel);
		paramRightPanel[iPanel+2].add(sliderX);
		paramRightPanel[iPanel+2].setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder(Color.black), "Position X"));
		paramRightPanel[iPanel+3].add(pPosYLabel);
		paramRightPanel[iPanel+3].add(sliderY);
		paramRightPanel[iPanel+3].setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder(Color.black), "Position Y"));
		paramRightPanel[iPanel+4].add(pVXLabel);
		paramRightPanel[iPanel+4].add(sliderVx);
		paramRightPanel[iPanel+4].setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder(Color.black), "Velocity X"));
		paramRightPanel[iPanel+5].add(pVYLabel);
		paramRightPanel[iPanel+5].add(sliderVy);
		paramRightPanel[iPanel+5].setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder(Color.black), "Velocity Y"));
		
		simTimeLabel = new JLabel("T(s): ");
		simTimeSlider = new JSlider(JSlider.HORIZONTAL,0,30,15);
		simTimeSlider.setMajorTickSpacing(10);
		simTimeSlider.setMinorTickSpacing(5);
		simTimeSlider.setPaintTicks(true);
		simTimeSlider.setPaintLabels(true);
		simTimeSlider.addChangeListener(e -> {
			simulationTime = simTimeSlider.getValue();
		});
		
		iRightPanel[iPanel+2].add(simTimeLabel);
		iRightPanel[iPanel+2].add(simTimeSlider);
		iRightPanel[iPanel+2].setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder(Color.black),
				"Time of Simulation", TitledBorder.CENTER, TitledBorder.TOP));
		
		startButton = new JButton("START");
		startButton.setOpaque(true);
		startButton.setBackground(Color.GREEN);
		startButton.addActionListener(e -> {
			dt=0;
			drawablePanel.initializeVectorForNewSimulation();
			STOPSIMULATION = false;
			isVectorInitialize = true;
			ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
			scheduler.scheduleAtFixedRate( (new Runnable() {
				@Override
				public void run() {
					if(!STOPSIMULATION){
						drawablePanel.startSimulation();

						sliderX.setValue((int)Math.round(body[selectedBody].getxPos()));
						sliderY.setValue((int)Math.round(body[selectedBody].getyPos()));
						sliderVx.setValue((int)Math.round(body[selectedBody].getvX()));
						sliderVy.setValue((int)Math.round(body[selectedBody].getvY()));
						if(dt == simulationTime*1000) {
							STOPSIMULATION = true;
						}
					}
					dt+=50;
				}
			}), 0, 50, MILLISECONDS);
		});
		endButton = new JButton("STOP");
		endButton.setOpaque(true);
		endButton.addActionListener(e -> {
			STOPSIMULATION = true;
			StopPushed = true;
		});
		endButton.setBackground(Color.RED);
		
		iRightPanel[iPanel+3].add(startButton);
		iRightPanel[iPanel+3].add(endButton);

		// setting database connection
		dataBase = new DataBase();
    }
    public GUI(GraphicsConfiguration gc) {
        super(gc);
        this.setSize(WIDTH, HEIGHT);
        this.setResizable(false);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }
    public GUI(String title) throws HeadlessException {
        super(title);
        this.setSize(WIDTH, HEIGHT);
        this.setResizable(false);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }
    public GUI(String title, GraphicsConfiguration gc) {
        super(title, gc);
        this.setSize(WIDTH, HEIGHT);
        this.setResizable(false);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }


    // HERE STARTS METHODS=========================================================
    private void makeDrawablePanel() {
		drawablePanel = new DrawablePanel(body);
		drawablePanel.setPreferredSize(new Dimension(1000, 660));
		drawablePanel.beginDrawing();
		leftPanel.add(drawablePanel);
	}

	private void makeBodyArray(int xPos, int yPos, int vX, int vY, int mass) {
		body = new Body[numSimulatedBody];

		bodyListVector = new Vector<>();

    	for (int i = 0; i < numSimulatedBody; i++) {
			body[i] = new Body(xPos, yPos, vX, vY, mass);
			bodyListVector.add(body[i]);
    	}
	}

	private void makeRandomBodyArray() {
    	body = new Body[numSimulatedBody];

    	bodyListVector = new Vector<>();

		for(int i = 0; i < numSimulatedBody; i++) {
			int xPos = ThreadLocalRandom.current().nextInt(0, 1000 + 1);
			int yPos = ThreadLocalRandom.current().nextInt(0, 660 + 1);
			int vX = ThreadLocalRandom.current().nextInt(-20, 20 + 1);
			int vY = ThreadLocalRandom.current().nextInt(-20, 20 + 1);
			int mass = ThreadLocalRandom.current().nextInt(0, 100 + 1);

			body[i] = new Body(xPos, yPos, vX, vY, mass);

			bodyListVector.add(body[i]);
		}
	}


	private void setSliders(int item) {
		sliderX.setValue((int)Math.round(body[item].getxPos()));
		sliderY.setValue((int)Math.round(body[item].getyPos()));
		sliderVx.setValue((int)Math.round(body[item].getvX()));
		sliderVy.setValue((int)Math.round(body[item].getvY()));
		pMassText.setText(Integer.toString(body[item].getMass()));
	}

    ActionListener examplesListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent actionEvent) {
			if(actionEvent.getSource() == menuUserSettings) {
				makeBodyArray(496, 326, 0, 0, 100);
				numSimulatedBody = 2;
			}
			if(actionEvent.getSource() == menuExample2) {
				makeRandomBodyArray();
				numSimulatedBody = 2;
			}
			if(actionEvent.getSource() == menuExample4) {
				makeRandomBodyArray();
				numSimulatedBody = 4;
			}
			if(actionEvent.getSource() == menuExample6) {
				makeRandomBodyArray();
				numSimulatedBody = 6;
			}
			if(actionEvent.getSource() == menuExample9) {
				makeRandomBodyArray();
				numSimulatedBody = 9;
			}
			//always set new Vectors for saving data from simulation
			drawablePanel.initializeVectorForNewSimulation();

			// same actions for every menuExample, code is very similar to pNummerSpinnerListener
			selectedBody = 0;
			pNumberSpinner.setValue(numSimulatedBody);
			particleNumbers = new String[numSimulatedBody];
			for (int i = 0; i < numSimulatedBody; i++) {
				particleNumbers[i] = Integer.toString(i+1);
			}
			numberBox.setModel(new DefaultComboBoxModel<String>(particleNumbers));
			leftPanel.remove(drawablePanel);
			makeDrawablePanel();
			setSliders(0);
		}
	};
}