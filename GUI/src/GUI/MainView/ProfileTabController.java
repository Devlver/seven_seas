package GUI.MainView;

import GUI.Network.Account;

class ProfileTabController {
	private MainViewController main;
	
	ProfileTabController(MainViewController main) {
		this.main = main;
	}
	
	void PopulateTab() {
		main.SetLoading(true);
		
		main.getProfileNameLabel().setText(Account.getFullName());
		main.getProfileUsernameLabel().setText(Account.getUsername());
		main.getProfileCabinLabel().setText(Account.getCabinNumber());
		main.getProfileEmailLabel().setText(Account.getEmail());
	}
}
