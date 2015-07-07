# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.db import models, migrations


class Migration(migrations.Migration):

    dependencies = [
        ('server', '0003_skills_users'),
    ]

    operations = [
        migrations.CreateModel(
            name='Users_skills',
            fields=[
                ('id', models.AutoField(verbose_name='ID', serialize=False, auto_created=True, primary_key=True)),
                ('skills', models.ManyToManyField(to='server.Skills')),
                ('users', models.ManyToManyField(to='server.Users')),
            ],
        ),
    ]
