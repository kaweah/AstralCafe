package com.kaweah.astralcafe.app;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

import com.kaweah.astralcafe.starbinder.SimpleBinder;
import com.kaweah.astralcafe.starbinder.StarBinder;

@SuppressWarnings("serial")
public class UserDefinedPatternConfigDialog extends JDialog implements ActionListener
{
	private JTextField minScaleEditField = new JTextField();
	private JTextField maxScaleEditField = new JTextField();
	private JTextField scalingStepsEditField = new JTextField();

	public UserDefinedPatternConfigDialog(AstralFrame owner)
	{
		super(owner, "User-Defined Pattern Config", true);
		
		Container contentPane = getContentPane();
				
		JPanel settingsPanel = new JPanel();
		JPanel buttonPanel = new JPanel();
		contentPane.add(settingsPanel, BorderLayout.CENTER);
		contentPane.add(buttonPanel, BorderLayout.SOUTH);

		settingsPanel.setLayout(new GridBagLayout());		
		GridBagConstraints cc = new GridBagConstraints();
		
		JLabel minScaleLabel = new JLabel("Minimum Scale");
		JLabel maxScaleLabel = new JLabel("Maximum Scale");
		JLabel scalingStepsLabel = new JLabel("Scaling Steps");
		
		StarBinder binder = owner.getCanvas().getStarBinder();
				
		minScaleEditField.setText(" " + binder.minScale);
		maxScaleEditField.setText(" " + binder.maxScale);
		scalingStepsEditField.setText(" " + binder.scalingSteps);
		
		cc.gridx = 1;
		cc.gridy = 1;
		
		settingsPanel.add(minScaleLabel, cc);
		cc.gridy = 2;
		settingsPanel.add(maxScaleLabel, cc);
		cc.gridy = 3;
		settingsPanel.add(scalingStepsLabel, cc);
		cc.gridx = 2;
		cc.gridy = 1;
		settingsPanel.add(new JLabel(" "), cc);
		cc.gridx = 3;
		settingsPanel.add(minScaleEditField, cc);
		cc.gridy = 2;
		settingsPanel.add(maxScaleEditField, cc);
		cc.gridy = 3;
		settingsPanel.add(scalingStepsEditField, cc);
		
		JButton okButton = new JButton("OK");
		JButton cancelButton = new JButton("Cancel");
		
		okButton.addActionListener(this);
		cancelButton.addActionListener(this);
		
		buttonPanel.add(okButton);
		buttonPanel.add(cancelButton);
		
		int width = 300;
		int height = 250;
		int x = owner.getX()+(owner.getWidth()-width) / 2;
		int y = owner.getY()+(owner.getHeight()-height) / 2;
		setBounds(x, y, width, height);
		setVisible(true);		
	}

	public void actionPerformed(ActionEvent e)
	{
		String command = e.getActionCommand();
		
		if ("OK".equals(command))
		{
			int lowerMaxDistance = Integer.valueOf(minScaleEditField.getText().trim());
			int upperMaxDistance = Integer.valueOf(maxScaleEditField.getText().trim());
			int maxLinksPerStar  = Integer.valueOf(scalingStepsEditField.getText().trim());
			
			if (lowerMaxDistance != SimpleBinder.lowerMaxDistance ||
				upperMaxDistance != SimpleBinder.upperMaxDistance ||
				maxLinksPerStar  != SimpleBinder.maxLinksPerStar)
			{
				SimpleBinder.lowerMaxDistance = lowerMaxDistance;
				SimpleBinder.upperMaxDistance = upperMaxDistance;
				SimpleBinder.maxLinksPerStar  = maxLinksPerStar;
				// canvas needs to be refreshed/repainted
			}
		}
		
		setVisible(false);
	}
}
