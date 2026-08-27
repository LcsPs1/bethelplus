import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';

class MainScaffold extends StatelessWidget {
  final Widget child;

  const MainScaffold({super.key, required this.child});

  static const List<_NavItem> _navItems = [
    _NavItem(path: '/home', icon: Icons.home_rounded, label: 'Accueil'),
    _NavItem(path: '/bible', icon: Icons.menu_book_rounded, label: 'Bible'),
    _NavItem(path: '/meditation', icon: Icons.self_improvement_rounded, label: 'Méditation'),
    _NavItem(path: '/journal', icon: Icons.book_rounded, label: 'Journal'),
    _NavItem(path: '/profile', icon: Icons.person_rounded, label: 'Profil'),
  ];

  int _currentIndex(BuildContext context) {
    final location = GoRouterState.of(context).matchedLocation;
    for (int i = 0; i < _navItems.length; i++) {
      if (location.startsWith(_navItems[i].path)) return i;
    }
    return 0;
  }

  @override
  Widget build(BuildContext context) {
    final currentIndex = _currentIndex(context);

    return Scaffold(
      body: child,
      bottomNavigationBar: NavigationBar(
        selectedIndex: currentIndex,
        onDestinationSelected: (index) {
          context.go(_navItems[index].path);
        },
        destinations: _navItems.map((item) {
          return NavigationDestination(
            icon: Icon(item.icon),
            label: item.label,
          );
        }).toList(),
      ),
    );
  }
}

class _NavItem {
  final String path;
  final IconData icon;
  final String label;

  const _NavItem({
    required this.path,
    required this.icon,
    required this.label,
  });
}
